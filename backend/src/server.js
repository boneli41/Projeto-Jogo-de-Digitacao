import express from "express";
import cors from "cors";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { exercisesRouter } from "./routes/exercises.js";
import { rankingRouter } from "./routes/ranking.js";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const SITE_DIR = path.join(__dirname, "..", "..", "site");

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors());
app.use(express.json());

app.use("/api/exercises", exercisesRouter);
app.use("/api/ranking", rankingRouter);
app.get("/api/health", (req, res) => res.json({ ok: true }));

// Site estático (index.html, jogo.html, resultado.html, style.css, script.js, assets/)
app.use(express.static(SITE_DIR));

app.listen(PORT, () => {
  console.log(`Digita Comigo rodando em http://localhost:${PORT}`);
});
