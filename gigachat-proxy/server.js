const express = require("express");
const cors = require("cors");
const axios = require("axios");
const crypto = require("crypto");
const fs = require("fs");
const path = require("path");
const https = require("https");
const multer = require("multer");
const FormData = require("form-data");

require("dotenv").config();

const app = express();

app.use(cors());
app.use(express.json());

const upload = multer({ storage: multer.memoryStorage() });

const certificatePath = path.join(
  __dirname,
  "certs",
  "russian_trusted_root_ca.cer"
);

const httpsAgent = new https.Agent({
  ca: fs.readFileSync(certificatePath),
});

app.get("/health", (req, res) => {
  res.json({
    status: "ok",
    message: "gigachat proxy is running",
  });
});

async function getAccessToken() {
  const authKey = process.env.GIGACHAT_AUTH_KEY;

  if (!authKey) {
    throw new Error("GIGACHAT_AUTH_KEY is missing in .env");
  }

  const body = new URLSearchParams();
  body.append("scope", "GIGACHAT_API_PERS");

  const response = await axios.post(
    "https://ngw.devices.sberbank.ru:9443/api/v2/oauth",
    body.toString(),
    {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        "Accept": "application/json",
        "RqUID": crypto.randomUUID(),
        "Authorization": `Basic ${authKey}`,
      },
      httpsAgent: httpsAgent,
    }
  );

  return response.data;
}

function buildSystemPrompt(mode, goal) {
  if (mode === "MEAL_CALORIES") {
    return `Ты — AI-помощник в мобильном приложении по питанию.

Пользователь отправляет фотографию блюда.
Твоя задача:
- определить, что это может быть за блюдо;
- примерно оценить калорийность;
- если точность ограничена, прямо сказать, что оценка примерная;
- отвечать кратко, понятно и по делу;
- отвечать на русском языке.`;
  }

  if (mode === "DISH_SUGGESTION") {
    let goalText = "без конкретной цели";

    if (goal === "LOSE_WEIGHT") {
      goalText = "похудение";
    } else if (goal === "MAINTAIN_WEIGHT") {
      goalText = "поддержание веса";
    } else if (goal === "GAIN_WEIGHT") {
      goalText = "набор массы";
    }

    return `Ты — AI-помощник в мобильном приложении по питанию.

Пользователь отправляет фотографию набора продуктов.
Твоя задача:
- определить, какие продукты видны на фото;
- предложить, что можно из них приготовить;
- учитывать цель пользователя: ${goalText};
- отвечать кратко, понятно и практично;
- отвечать на русском языке.`;
  }

  return `Ты — AI-помощник в мобильном приложении по питанию, калориям и выбору блюд.

Твои задачи:
- помогать пользователю по теме питания, калорий, выбора блюд, рациона, набора массы и похудения;
- отвечать только по делу и по теме запроса;
- не додумывать эмоции пользователя, если он их не выражал;
- не уходить в психологию, если вопрос обычный;
- если точность ограничена, прямо писать, что оценка примерная;
- если вопрос не связан с питанием, всё равно отвечать спокойно и кратко как встроенный помощник приложения.

Правила ответа:
- отвечай на русском языке;
- пиши понятно, кратко и доброжелательно;
- обычно укладывайся в 2–5 предложений;
- если вопрос про еду, калории или блюда — отвечай именно как помощник по питанию;
- не используй странные интерпретации и не драматизируй.`;
}

async function sendChatMessage(accessToken, userMessage, mode, goal) {
  const response = await axios.post(
    "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
    {
      model: "GigaChat-2-Pro",
      messages: [
        {
          role: "system",
          content: buildSystemPrompt(mode, goal),
        },
        {
          role: "user",
          content: userMessage,
        },
      ],
      stream: false,
    },
    {
      headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Authorization": `Bearer ${accessToken}`,
      },
      httpsAgent: httpsAgent,
    }
  );

  return response.data;
}

async function uploadImageToGigaChat(accessToken, file) {
  const form = new FormData();

  form.append("file", file.buffer, {
    filename: file.originalname || "image.jpg",
    contentType: file.mimetype || "image/jpeg",
  });

  form.append("purpose", "general");

  const response = await axios.post(
    "https://gigachat.devices.sberbank.ru/api/v1/files",
    form,
    {
      headers: {
        ...form.getHeaders(),
        Accept: "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      httpsAgent: httpsAgent,
      maxBodyLength: Infinity,
    }
  );

  return response.data;
}

async function sendChatMessageWithImage(accessToken, userMessage, fileId, mode, goal) {
  const response = await axios.post(
    "https://gigachat.devices.sberbank.ru/api/v1/chat/completions",
    {
      model: "GigaChat-2-Pro",
      messages: [
        {
          role: "system",
          content: buildSystemPrompt(mode, goal),
        },
        {
          role: "user",
          content: userMessage,
          attachments: [fileId],
        },
      ],
      stream: false,
    },
    {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      httpsAgent: httpsAgent,
    }
  );

  return response.data;
}

app.get("/api/token/test", async (req, res) => {
  try {
    const tokenData = await getAccessToken();

    res.json({
      success: true,
      tokenData,
    });
  } catch (error) {
    console.error("Token test failed:", error.response?.data || error.message);

    res.status(500).json({
      success: false,
      error: error.response?.data || error.message,
    });
  }
});

app.post("/api/chat/text", async (req, res) => {
  try {
    const { message, mode, goal } = req.body;

    if (!message || !message.trim()) {
      return res.status(400).json({
        success: false,
        error: "Message is required",
      });
    }

    const tokenData = await getAccessToken();

    const chatData = await sendChatMessage(
      tokenData.access_token,
      message,
      mode || "DEFAULT",
      goal || null
    );

    const answer =
      chatData?.choices?.[0]?.message?.content ||
      "GigaChat вернул пустой ответ";

    res.json({
      success: true,
      answer,
      raw: chatData,
    });
  } catch (error) {
    console.error("Text chat failed:", error.response?.data || error.message);

    res.status(500).json({
      success: false,
      error: error.response?.data || error.message,
    });
  }
});

app.post("/api/chat/image", upload.single("image"), async (req, res) => {
  try {
    const file = req.file;
    const { message, mode, goal } = req.body;

    if (!file) {
      return res.status(400).json({
        success: false,
        error: "Image file is required",
      });
    }

    const tokenData = await getAccessToken();

    const uploadedFile = await uploadImageToGigaChat(
      tokenData.access_token,
      file
    );

    const fileId = uploadedFile?.id;

    if (!fileId) {
      throw new Error("Failed to upload image to GigaChat");
    }

    const safeMessage =
      message && message.trim()
        ? message
        : mode === "MEAL_CALORIES"
          ? "Определи блюдо на фото и оцени его примерную калорийность."
          : "Определи продукты на фото и предложи, что можно приготовить.";

    const chatData = await sendChatMessageWithImage(
      tokenData.access_token,
      safeMessage,
      fileId,
      mode || "DEFAULT",
      goal || null
    );

    const answer =
      chatData?.choices?.[0]?.message?.content ||
      "GigaChat вернул пустой ответ";

    res.json({
      success: true,
      answer,
      raw: chatData,
      uploadedFileId: fileId,
    });
  } catch (error) {
    console.error("Image chat failed:", error.response?.data || error.message);

    res.status(500).json({
      success: false,
      error: error.response?.data || error.message,
    });
  }
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
});