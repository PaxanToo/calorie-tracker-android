const express = require("express");
const cors = require("cors");
const axios = require("axios");
const multer = require("multer");

require("dotenv").config();

const app = express();

app.use(cors());
app.use(express.json({ limit: "10mb" }));

const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 10 * 1024 * 1024,
  },
});

const PORT = process.env.PORT || 3000;
const OLLAMA_BASE_URL = process.env.OLLAMA_BASE_URL || "http://127.0.0.1:11434";
const OLLAMA_MODEL = process.env.OLLAMA_MODEL || "qwen3.5:4b";

app.get("/health", async (req, res) => {
  try {
    const response = await axios.get(`${OLLAMA_BASE_URL}/api/tags`, {
      timeout: 5000,
    });

    res.json({
      status: "ok",
      message: "ollama proxy is running",
      model: OLLAMA_MODEL,
      ollamaReachable: true,
      modelsCount: response.data?.models?.length || 0,
    });
  } catch (error) {
    res.status(500).json({
      status: "error",
      message: "ollama proxy is running, but Ollama is unavailable",
      model: OLLAMA_MODEL,
      ollamaReachable: false,
      error: error.message,
    });
  }
});

function buildSystemPrompt(mode, goal) {
  if (mode === "MEAL_CALORIES") {
    return `Ты — AI-помощник в мобильном приложении по питанию.

Пользователь отправляет фотографию блюда.
Твоя задача:
- определить, что это может быть за блюдо или набор продуктов на тарелке;
- дать примерную оценку КБЖУ всего блюда или порции на фото;
- если точность ограничена, прямо написать, что оценка примерная;
- не придумывать ингредиенты, которых не видно;
- отвечать кратко, понятно и на русском языке.

Сделай ответ СТРОГО в такой структуре:

1. Что на фото
Коротко опиши, что, вероятнее всего, изображено.

2. Примерная оценка КБЖУ
Калории: ~N ккал
Белки: ~N г
Жиры: ~N г
Углеводы: ~N г

3. Комментарий
1–2 коротких предложения о точности оценки или составе блюда.

Если точно определить блюдо нельзя, всё равно дай наиболее вероятную оценку и прямо укажи, что она приблизительная.`;
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
- определить, какие продукты вероятнее всего видны на фото;
- не выдумывать экзотические ингредиенты, если их не видно;
- предложить 1–3 простых блюда, которые реально можно приготовить из этих продуктов;
- учитывать цель пользователя: ${goalText};
- если каких-то ингредиентов не хватает, коротко укажи, что можно добавить;
- отвечать на русском языке;
- отвечать понятно, практично и без лишней воды.

Сделай ответ в такой структуре:
1. Что видно на фото
2. Что можно приготовить
3. Короткий комментарий по цели пользователя

Если уверенность невысокая, прямо скажи, что распознавание приблизительное.`;
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
- Не делай вид, что помнишь предыдущие сообщения пользователя, если они не переданы в текущем запросе; если для точного ответа не хватает контекста, прямо попроси пользователя кратко уточнить детали.;
- не используй странные интерпретации и не драматизируй.`;
}

function buildUserPrompt(userMessage, mode, goal) {
  const systemPrompt = buildSystemPrompt(mode, goal);

  if (!userMessage || !userMessage.trim()) {
    return systemPrompt;
  }

  return `${systemPrompt}

Запрос пользователя:
${userMessage.trim()}`;
}

async function generateText(prompt) {
  const response = await axios.post(
    `${OLLAMA_BASE_URL}/api/generate`,
    {
      model: OLLAMA_MODEL,
      prompt,
      stream: false,
      think: false,
      keep_alive: "30m",
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
      timeout: 180000,
      maxBodyLength: Infinity,
    }
  );

  return response.data;
}

async function generateWithImage(prompt, fileBuffer) {
  const base64Image = fileBuffer.toString("base64");

  const response = await axios.post(
    `${OLLAMA_BASE_URL}/api/generate`,
    {
      model: OLLAMA_MODEL,
      prompt,
      images: [base64Image],
      stream: false,
      think: false,
      keep_alive: "30m",
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
      timeout: 240000,
      maxBodyLength: Infinity,
    }
  );

  return response.data;
}

app.post("/api/chat/text", async (req, res) => {
  try {
    const { message, mode, goal } = req.body;

    if (!message || !message.trim()) {
      return res.status(400).json({
        success: false,
        error: "Message is required",
      });
    }

    const prompt = buildUserPrompt(
      message,
      mode || "DEFAULT",
      goal || null
    );

    const ollamaData = await generateText(prompt);

    const answer =
      ollamaData?.response?.trim() || "Ollama вернул пустой ответ";

    res.json({
      success: true,
      answer,
      raw: ollamaData,
    });
  } catch (error) {
    console.error(
      "Text chat failed:",
      error.response?.data || error.message
    );

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

    const safeMessage =
      message && message.trim()
        ? message.trim()
        : mode === "MEAL_CALORIES"
          ? "Определи блюдо на фото и оцени его примерные калории, белки, жиры и углеводы."
          : "Определи продукты на фото и предложи, что можно приготовить.";

    const prompt = buildUserPrompt(
      safeMessage,
      mode || "DEFAULT",
      goal || null
    );

    const ollamaData = await generateWithImage(prompt, file.buffer);

    const answer =
      ollamaData?.response?.trim() || "Ollama вернул пустой ответ";

    res.json({
      success: true,
      answer,
      raw: ollamaData,
    });
  } catch (error) {
    console.error(
      "Image chat failed:",
      error.response?.data || error.message
    );

    res.status(500).json({
      success: false,
      error: error.response?.data || error.message,
    });
  }
});

app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
  console.log(`Using Ollama model: ${OLLAMA_MODEL}`);
  console.log(`Ollama base URL: ${OLLAMA_BASE_URL}`);
});