const express = require("express");
const cors = require("cors");
const axios = require("axios");
const crypto = require("crypto");
require("dotenv").config();
const fs = require("fs");
const path = require("path");
const https = require("https");

const app = express();

app.use(cors());
app.use(express.json());

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

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server started on port ${PORT}`);
});