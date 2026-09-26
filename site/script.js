// Digita Comigo — script.js
// Todas as ações do site (menu, jogo, resultado) vivem neste único arquivo.
// Cada página HTML só define o <body data-page="..."> e os elementos;
// quem cuida do comportamento é este script.

"use strict";

/* ============================================================
   1. LÓGICA DO JOGO (fórmulas de XP, estrelas, vidas — igual ao Java)
   ============================================================ */

const MAX_LIVES = 3;

function createPlayer(name) {
  return { name, lives: MAX_LIVES, totalScore: 0, exercisesCompleted: 0, streak: 0, achievements: [] };
}

function checkAchievements(player) {
  const add = (name) => { if (!player.achievements.includes(name)) player.achievements.push(name); };
  if (player.exercisesCompleted >= 1) add("Primeiro Passo");
  if (player.exercisesCompleted >= 5) add("Praticante");
  if (player.exercisesCompleted >= 10) add("Dedicado");
  if (player.streak >= 3) add("Em Sequência");
  if (player.streak >= 5) add("Imparável");
  if (player.totalScore >= 500) add("500 Pontos");
}

function addXP(player, amount) {
  player.totalScore += amount;
  checkAchievements(player);
}

function loseLife(player) {
  if (player.lives > 0) player.lives--;
  player.streak = 0;
}

function completeExercise(player) {
  player.exercisesCompleted++;
  player.streak++;
  checkAchievements(player);
}

function isGameOver(player) { return player.lives <= 0; }
function recordSessionStats(player, { seconds, typed, target, stars = 0, completed = false, moduleCompleted = false }) {
  const stats = player.sessionStats ||= { seconds: 0, characters: 0, correct: 0, stars: 0, attempts: 0, bestStreak: 0, modules: 0 };
  stats.seconds += seconds;
  stats.characters += typed.length;
  stats.correct += countCorrect(typed, target);
  stats.stars += stars;
  stats.attempts += completed ? 1 : 0;
  stats.bestStreak = Math.max(stats.bestStreak, player.streak);
  stats.modules += moduleCompleted ? 1 : 0;
}

function calculateScore(correctChars, totalChars, timeUsedMs, xpReward, timeLimit) {
  if (totalChars === 0) return 0;
  const accuracy = correctChars / totalChars;
  const timeFraction = timeUsedMs / (timeLimit * 1000);
  const timeBonus = Math.max(0, 1 - timeFraction) * 0.5;
  return Math.floor(xpReward * accuracy * (1 + timeBonus));
}

function calculateStars(accuracy, timeUsedMs, timeLimit) {
  const timeFraction = timeUsedMs / (timeLimit * 1000);
  if (accuracy >= 0.95 && timeFraction <= 0.7) return 3;
  if (accuracy >= 0.8) return 2;
  if (accuracy >= 0.6) return 1;
  return 0;
}

function countCorrect(typed, target) {
  let n = 0;
  const len = Math.min(typed.length, target.length);
  for (let i = 0; i < len; i++) if (typed[i] === target[i]) n++;
  return n;
}

function heartsString(lives) {
  return Array.from({ length: MAX_LIVES }, (_, i) => (i < lives ? "❤" : "♡")).join(" ");
}

function starsString(stars) {
  return Array.from({ length: 3 }, (_, i) => (i < stars ? "★" : "☆")).join(" ");
}

function pickRandom(bank) { return bank[Math.floor(Math.random() * bank.length)]; }

const MSGS_CORRECT = ["Muito bem!  Continue assim!", "Isso aí!  Você está indo bem!", "Perfeito!  Próxima letra!", "Ótimo!  Continue no seu ritmo!", "Show!  Você acertou!"];
const MSGS_WRONG = ["Ops! Procure a tecla certa com calma.", "Quase lá! Olhe o teclado e tente de novo.", "Sem pressa, procure a tecla certinha.", "Não desanime, respire e tente outra vez."];
const MSGS_3_STARS = ["Perfeito! Você arrasou!", "Excelente! Você é demais!", "Incrível! Continue assim!", "Sensacional! Está cada vez melhor!"];
const MSGS_2_STARS = ["Muito bem! Ótimo trabalho!", "Muito bom! Você está evoluindo!", "Parabéns! Boa digitação!", "Ótimo! Continue praticando!"];
const MSGS_1_STAR = ["Bom início! Vai melhorar!", "Você conseguiu! Continue tentando!", "Foi um começo! Vamos praticar mais!", "Está no caminho certo!"];
const MSGS_0_STARS = ["Tente novamente, não desista!", "Calma, vamos tentar de novo!", "Não desanime, a prática leva à perfeição!", "Respire e tente outra vez, você consegue!"];

/* ============================================================
   2. TECLADO VISUAL ABNT2 (layout + destaque de tecla)
   ============================================================ */

const U = 1;
const KEYS = [
  ["'", '"', 0, 0, U, U, false], ["1", "!", 1, 0, U, U, false], ["2", "@", 2, 0, U, U, false],
  ["3", "#", 3, 0, U, U, false], ["4", "$", 4, 0, U, U, false], ["5", "%", 5, 0, U, U, false],
  ["6", "¨", 6, 0, U, U, false], ["7", "&", 7, 0, U, U, false], ["8", "*", 8, 0, U, U, false],
  ["9", "(", 9, 0, U, U, false], ["0", ")", 10, 0, U, U, false], ["-", "_", 11, 0, U, U, false],
  ["=", "+", 12, 0, U, U, false], ["Apagar", "", 13, 0, 2, U, true],

  ["Tab", "", 0, 1, 1.5, U, true], ["Q", "", 1.5, 1, U, U, false], ["W", "", 2.5, 1, U, U, false],
  ["E", "", 3.5, 1, U, U, false], ["R", "", 4.5, 1, U, U, false], ["T", "", 5.5, 1, U, U, false],
  ["Y", "", 6.5, 1, U, U, false], ["U", "", 7.5, 1, U, U, false], ["I", "", 8.5, 1, U, U, false],
  ["O", "", 9.5, 1, U, U, false], ["P", "", 10.5, 1, U, U, false], ["´", "`", 11.5, 1, U, U, false],
  ["[", "{", 12.5, 1, U, U, false], ["Enter", "", 13.5, 1, 1.5, 2, true],

  ["Caps", "", 0, 2, 1.8, U, true], ["A", "", 1.8, 2, U, U, false], ["S", "", 2.8, 2, U, U, false],
  ["D", "", 3.8, 2, U, U, false], ["F", "", 4.8, 2, U, U, false], ["G", "", 5.8, 2, U, U, false],
  ["H", "", 6.8, 2, U, U, false], ["J", "", 7.8, 2, U, U, false], ["K", "", 8.8, 2, U, U, false],
  ["L", "", 9.8, 2, U, U, false], ["Ç", "", 10.8, 2, U, U, false], ["~", "^", 11.8, 2, U, U, false],
  ["]", "}", 12.8, 2, U, U, false],

  ["Shift", "", 0, 3, 1.3, U, true], ["|", "\\", 1.3, 3, U, U, false], ["Z", "", 2.3, 3, U, U, false],
  ["X", "", 3.3, 3, U, U, false], ["C", "", 4.3, 3, U, U, false], ["V", "", 5.3, 3, U, U, false],
  ["B", "", 6.3, 3, U, U, false], ["N", "", 7.3, 3, U, U, false], ["M", "", 8.3, 3, U, U, false],
  [",", "<", 9.3, 3, U, U, false], [".", ">", 10.3, 3, U, U, false], [";", ":", 11.3, 3, U, U, false],
  ["/", "?", 12.3, 3, U, U, false], ["Shift", "", 13.3, 3, 1.7, U, true],

  ["Ctrl", "", 0, 4, 1.3, U, true], ["Win", "", 1.3, 4, 1.2, U, true], ["Alt", "", 2.5, 4, 1.2, U, true],
  ["", "", 3.7, 4, 6.3, U, false], ["AltGr", "", 10.0, 4, 1.2, U, true], ["Win", "", 11.2, 4, 1.0, U, true],
  ["Menu", "", 12.2, 4, 0.9, U, true], ["Ctrl", "", 13.1, 4, 0.9, U, true],
].map(([label, shiftLabel, x, y, w, h, special]) => ({ label, shiftLabel, x, y, w, h, special }));

const TOTAL_UNITS_X = 15;
const TOTAL_ROWS = 5;

const charToKey = new Map();
const needsShift = new Set();
let idxShiftL = -1, idxShiftR = -1, idxBackspace = -1, idxSpace = -1;

function buildCharMap() {
  KEYS.forEach((k, i) => {
    if (k.special) {
      if (k.label === "Shift" && idxShiftL < 0) idxShiftL = i;
      else if (k.label === "Shift") idxShiftR = i;
      else if (k.label === "Apagar") idxBackspace = i;
      return;
    }
    if (k.label) {
      const c = k.label[0];
      if (c >= "A" && c <= "Z") {
        charToKey.set(c.toLowerCase(), i);
        charToKey.set(c, i);
        needsShift.add(c);
      } else {
        charToKey.set(c, i);
        if (c === "´") {
          for (const ac of "áéíóú") charToKey.set(ac, i);
          for (const ac of "ÁÉÍÓÚ") { charToKey.set(ac, i); needsShift.add(ac); }
          for (const ac of "àèìòù") { charToKey.set(ac, i); needsShift.add(ac); }
          for (const ac of "ÀÈÌÒÙ") { charToKey.set(ac, i); needsShift.add(ac); }
          charToKey.set("`", i); needsShift.add("`");
        }
        if (c === "~") {
          for (const ac of "ãõñ") charToKey.set(ac, i);
          for (const ac of "ÃÕÑ") { charToKey.set(ac, i); needsShift.add(ac); }
          for (const ac of "âêîôû") { charToKey.set(ac, i); needsShift.add(ac); }
          for (const ac of "ÂÊÎÔÛ") { charToKey.set(ac, i); needsShift.add(ac); }
        }
      }
    }
    if (k.shiftLabel) {
      const c = k.shiftLabel[0];
      charToKey.set(c, i);
      needsShift.add(c);
    }
  });
  KEYS.forEach((k, i) => { if (k.y === 4 && k.w > 4) idxSpace = i; });
  if (idxSpace >= 0) charToKey.set(" ", idxSpace);
  KEYS.forEach((k, i) => { if (k.label === "Ç") charToKey.set("ç", i); });
}
buildCharMap();

const BASE_VOWEL = {
  á: "a", Á: "a", é: "e", É: "e", í: "i", Í: "i", ó: "o", Ó: "o", ú: "u", Ú: "u",
  à: "a", À: "a", è: "e", È: "e", ì: "i", Ì: "i", ò: "o", Ò: "o", ù: "u", Ù: "u",
  â: "a", Â: "a", ê: "e", Ê: "e", î: "i", Î: "i", ô: "o", Ô: "o", û: "u", Û: "u",
  ã: "a", Ã: "a", õ: "o", Õ: "o", ñ: "n", Ñ: "n",
};
function baseVowel(c) { return BASE_VOWEL[c] || null; }

function computeKeyStates({ expected, vowelHint, hasError }) {
  const states = new Array(KEYS.length).fill(null);
  if (hasError) {
    if (idxBackspace >= 0) states[idxBackspace] = "error";
    return states;
  }
  const mark = (ch) => {
    if (!ch) return;
    const idx = charToKey.get(ch);
    if (idx === undefined) return;
    states[idx] = needsShift.has(ch) ? "shift" : "normal";
  };
  mark(expected);
  mark(vowelHint);
  const anyShift = states.some((s, i) => s === "shift" && !KEYS[i].special);
  if (anyShift) {
    if (idxShiftL >= 0) states[idxShiftL] = "shift";
    if (idxShiftR >= 0) states[idxShiftR] = "shift";
  }
  return states;
}

const REFERENCE_HIGHLIGHT = new Set(["Tab", "Apagar", "Caps", "Enter", "Shift", "Ctrl", "Alt", "´", "~"]);

function renderKeyboard(container, { mode = "game", states = [] } = {}) {
  container.innerHTML = "";
  const board = document.createElement("div");
  board.className = "kb-board";

  KEYS.forEach((k, i) => {
    const key = document.createElement("div");
    key.className = "kb-key";
    if (k.special) key.classList.add("special");

    if (mode === "game") {
      const state = states[i];
      if (state === "error") key.classList.add("st-error");
      else if (state === "shift") key.classList.add("st-shift");
      else if (state === "normal") key.classList.add("st-normal");
    } else if (mode === "reference") {
      const isSpace = !k.special && !k.label;
      if (REFERENCE_HIGHLIGHT.has(k.label) || isSpace) key.classList.add("special");
    }

    key.style.left = `calc(${(k.x / TOTAL_UNITS_X) * 100}% + 2px)`;
    key.style.top = `calc(${(k.y / TOTAL_ROWS) * 100}% + 2px)`;
    key.style.width = `calc(${(k.w / TOTAL_UNITS_X) * 100}% - 4px)`;
    key.style.height = `calc(${(k.h / TOTAL_ROWS) * 100}% - 4px)`;

    const isSpaceKey = !k.special && !k.label;
    const labelSpan = document.createElement("span");
    labelSpan.textContent = isSpaceKey ? "ESPAÇO" : k.label;
    key.appendChild(labelSpan);

    if (k.shiftLabel) {
      const shiftSpan = document.createElement("span");
      shiftSpan.className = "shift-label";
      shiftSpan.textContent = k.shiftLabel;
      key.appendChild(shiftSpan);
    }

    board.appendChild(key);
  });

  container.appendChild(board);
}

/* ============================================================
   3. SONS (Web Audio, sem arquivos externos)
   ============================================================ */

const SOUND_KEY = "digitaComigo:soundEnabled";
function isSoundEnabled() {
  const v = localStorage.getItem(SOUND_KEY);
  return v === null ? true : v === "1";
}
function setSoundEnabled(enabled) { localStorage.setItem(SOUND_KEY, enabled ? "1" : "0"); }

let audioCtx = null;
function getAudioCtx() {
  if (!audioCtx) {
    const AudioCtx = window.AudioContext || window.webkitAudioContext;
    if (!AudioCtx) return null;
    audioCtx = new AudioCtx();
  }
  if (audioCtx.state === "suspended") audioCtx.resume();
  return audioCtx;
}

function beep({ freq, duration = 0.12, type = "sine", gain = 0.15, delay = 0 }) {
  if (!isSoundEnabled()) return;
  const ctx = getAudioCtx();
  if (!ctx) return;
  try {
    const startAt = ctx.currentTime + delay;
    const osc = ctx.createOscillator();
    const g = ctx.createGain();
    osc.type = type;
    osc.frequency.value = freq;
    g.gain.value = gain;
    osc.connect(g);
    g.connect(ctx.destination);
    osc.start(startAt);
    g.gain.exponentialRampToValueAtTime(0.0001, startAt + duration);
    osc.stop(startAt + duration);
  } catch { /* navegador pode bloquear áudio sem interação prévia */ }
}

function playCorrect() { beep({ freq: 880, duration: 0.07, gain: 0.1 }); }
function playWrong() { beep({ freq: 160, duration: 0.15, type: "square", gain: 0.1 }); }
function playErase() { beep({ freq: 340, duration: 0.05, type: "triangle", gain: 0.07 }); }
function playExit() { [520, 390, 260].forEach((freq, i) => beep({ freq, duration: 0.14, delay: i * 0.07, gain: 0.1 })); }
function playExerciseResult(stars) {
  const notes = stars >= 3 ? [523, 659, 784, 1047] : stars >= 1 ? [523, 659] : [220, 175];
  notes.forEach((freq, i) => beep({ freq, duration: 0.16, delay: i * 0.09 }));
}
function playModuleComplete() {
  [392, 494, 587, 784].forEach((freq, i) => beep({ freq, duration: 0.2, delay: i * 0.11 }));
}

/* ============================================================
   4. TEMA CLARO/ESCURO
   ============================================================ */

const THEME_KEY = "digitaComigo:theme";
function getTheme() { return localStorage.getItem(THEME_KEY) === "dark" ? "dark" : "light"; }
function applyTheme(theme) {
  document.documentElement.setAttribute("data-theme", theme);
  localStorage.setItem(THEME_KEY, theme);
}

function initThemeControl() {
  const button = document.getElementById("btn-theme");
  if (!button) return;
  button.type = "button";
  button.classList.add("theme-toggle");
  function render() {
    const dark = getTheme() === "dark";
    const label = dark ? "Claro" : "Escuro";
    const shape = dark
      ? '<circle cx="12" cy="12" r="4"/><path d="M12 2v2m0 16v2M2 12h2m16 0h2M5 5l1.5 1.5m11 11L19 19M5 19l1.5-1.5m11-11L19 5"/>'
      : '<path d="M20.5 13A8.5 8.5 0 0 1 11 3.5 8.5 8.5 0 1 0 20.5 13Z"/>';
    button.innerHTML = `<svg viewBox="0 0 24 24" aria-hidden="true" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${shape}</svg><span>${label}</span>`;
    button.setAttribute("aria-label", `Ativar modo ${label.toLowerCase()}`);
    button.title = `Ativar modo ${label.toLowerCase()}`;
  }
  button.addEventListener("click", () => {
    applyTheme(getTheme() === "dark" ? "light" : "dark");
    render();
  });
  render();
}

/* ============================================================
   5. ESTADO ENTRE PÁGINAS
   sessionStorage: estado da partida atual (perdido ao fechar a aba)
   localStorage:   autosave pra recuperar depois de fechar/crashar
   ============================================================ */

const SESSION_KEY = "digitaComigo:session";
const AUTOSAVE_KEY = "digitaComigo:autosave";

function saveSession(state) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(state));
  saveAutosave(state);
}
function loadSession() {
  const raw = sessionStorage.getItem(SESSION_KEY);
  return raw ? JSON.parse(raw) : null;
}
function clearSession() {
  sessionStorage.removeItem(SESSION_KEY);
  clearAutosave();
}

function saveAutosave(state) {
  try { localStorage.setItem(AUTOSAVE_KEY, JSON.stringify({ ...state, savedAt: Date.now() })); } catch { /* best-effort */ }
}
function loadAutosave() {
  try {
    const raw = localStorage.getItem(AUTOSAVE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch { return null; }
}
function clearAutosave() {
  try { localStorage.removeItem(AUTOSAVE_KEY); } catch { /* ignorado */ }
}

/* ============================================================
   6. API (backend Node — mesma origem, então caminho relativo)
   ============================================================ */

async function apiFetchCampaign(startLevel = 1) {
  const res = await fetch(`/api/exercises/campaign?startLevel=${startLevel}`);
  if (!res.ok) throw new Error("Falha ao carregar exercícios");
  return res.json();
}
async function apiFetchRankingTop(limit = 3) {
  const res = await fetch(`/api/ranking?limit=${limit}`);
  if (!res.ok) throw new Error("Falha ao carregar ranking");
  return res.json();
}
async function apiFetchRankingFull() {
  const res = await fetch(`/api/ranking/full`);
  if (!res.ok) throw new Error("Falha ao carregar ranking completo");
  return res.json();
}
async function apiPostResult(player) {
  const state = loadSession();
  const res = await fetch(`/api/ranking`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      name: player.name,
      totalScore: player.totalScore,
      // Compatibilidade com o ranking: o campo antigo agora representa o módulo atual.
      gameLevel: state?.exercises?.[state.exerciseIndex]?.moduleNumber || 1,
      streak: player.streak,
      exercisesCompleted: player.exercisesCompleted,
    }),
  });
  if (!res.ok) throw new Error("Falha ao salvar resultado");
  return res.json();
}
async function saveResultIfAny(player) {
  if (player && (player.totalScore > 0 || player.exercisesCompleted > 0)) {
    try { await apiPostResult(player); } catch { /* ranking é um extra, não trava o jogo */ }
  }
}

/* ============================================================
   7. PÁGINA: MENU (index.html)
   ============================================================ */

const MODULES = [
  { moduleNumber: 1, icon: "abc", name: "Minúsculas" },
  { moduleNumber: 2, icon: "ABC", name: "Maiúsculas" },
  { moduleNumber: 3, icon: "123", name: "Números" },
  { moduleNumber: 4, icon: "áéí", name: "Acentos" },
  { moduleNumber: 5, icon: ".,!", name: "Pontuação" },
];

const PROGRESSION_PAGE = "progressao-de-níveis.html";
const MODULE_LESSONS = {
  2: { ordinal: "SEGUNDO", title: "Letras maiúsculas", description: "Agora vamos combinar letras minúsculas e maiúsculas nas frases.", tip: "Segure Shift enquanto pressiona uma letra para digitá-la em maiúscula." },
  3: { ordinal: "TERCEIRO", title: "Números", description: "Chegou a hora de incluir números na sua digitação e praticar novas teclas.", tip: "Explore a fileira de números de 0 a 9, acima das letras do teclado." },
  4: { ordinal: "QUARTO", title: "Acentos", description: "Você vai praticar palavras com acentos e ganhar confiança na escrita em português.", tip: "Para digitar á, pressione o acento agudo e depois a letra a. Para ã, use o til e depois a letra a." },
  5: { ordinal: "QUINTO", title: "Pontuação", description: "Seu próximo desafio traz pontos, vírgulas e outros sinais para completar as frases.", tip: "Observe cada sinal: para os símbolos na parte superior de uma tecla, use Shift junto com ela." },
};

function getUpcomingModule(state) {
  if (!state?.resultData || state.sessionEnded || !state.player || isGameOver(state.player)) return null;
  const current = state.exercises?.[state.exerciseIndex];
  const next = state.exercises?.[state.exerciseIndex + 1];
  if (!current || !next || current.moduleNumber === next.moduleNumber) return null;
  return MODULES.find((module) => module.moduleNumber === next.moduleNumber) || null;
}

function showProgression(state) {
  if (!getUpcomingModule(state)) return false;
  saveSession({ ...state, progressionPending: true });
  window.location.href = PROGRESSION_PAGE;
  return true;
}

function initMenu() {
  applyTheme(getTheme());

  const btnSound = document.getElementById("btn-sound");
  const nameInput = document.getElementById("name-input");
  const btnStart = document.getElementById("btn-start");
  const journeyGrid = document.getElementById("journey-grid");
  const rankContent = document.getElementById("rank-content");
  const rankCard = document.getElementById("rank-card");
  const keyboardCard = document.getElementById("keyboard-card");
  const resumeBanner = document.getElementById("resume-banner");
  const btnResume = document.getElementById("btn-resume");
  const btnDiscardResume = document.getElementById("btn-discard-resume");
  const rankingModal = document.getElementById("ranking-modal");
  const rankingModalContent = document.getElementById("ranking-modal-content");
  const btnCloseRanking = document.getElementById("btn-close-ranking");
  const keyboardModal = document.getElementById("keyboard-modal");
  const btnCloseKeyboard = document.getElementById("btn-close-keyboard");
  const keyboardReferenceBoard = document.getElementById("keyboard-reference-board");
  [rankCard, keyboardCard].forEach((card) => {
    card.addEventListener("keydown", (event) => {
      if (event.key === "Enter" || event.key === " ") {
        event.preventDefault();
        card.click();
      }
    });
  });

  let selectedModule = 1;
  let starting = false;

  function renderSoundBtn() { btnSound.textContent = isSoundEnabled() ? "Som ligado" : "Som desligado"; }
  renderSoundBtn();

  btnSound.addEventListener("click", () => { setSoundEnabled(!isSoundEnabled()); renderSoundBtn(); });

  function renderJourney() {
    journeyGrid.innerHTML = "";
    MODULES.forEach((m) => {
      const btn = document.createElement("button");
      btn.className = "journey-item" + (m.moduleNumber === selectedModule ? " active" : "");
      btn.title = `Começar em: ${m.name}`;
      btn.setAttribute("aria-pressed", String(m.moduleNumber === selectedModule));
      btn.innerHTML = `<span class="icon">${m.icon}</span><span>${m.name}</span>`;
      btn.addEventListener("click", () => { selectedModule = m.moduleNumber; renderJourney(); });
      journeyGrid.appendChild(btn);
    });
  }
  renderJourney();

  function rankLineHtml(pos, name, score) {
    return `<div class="rank-line"><span class="pos">${pos}</span><span>${name}</span><span class="score">${score}</span></div>`;
  }

  apiFetchRankingTop(3)
    .then((rows) => {
      rankContent.innerHTML = rows.length === 0
        ? '<div class="rank-empty">Ainda sem pontuações</div>'
        : rows.map((r, i) => rankLineHtml(`${i + 1}º`, r.name, r.totalScore)).join("");
    })
    .catch(() => { rankContent.innerHTML = '<div class="rank-empty">Ainda sem pontuações</div>'; });

  rankCard.addEventListener("click", async () => {
    rankingModal.classList.remove("hidden");
    rankingModalContent.innerHTML = "Carregando...";
    try {
      const rows = await apiFetchRankingFull();
      rankingModalContent.innerHTML = rows.length === 0
        ? "Ainda não há pontuações."
        : rows.map((r, i) => rankLineHtml(`${i + 1}º`, r.name, r.totalScore)).join("");
    } catch {
      rankingModalContent.innerHTML = "Não foi possível carregar o ranking.";
    }
  });
  btnCloseRanking.addEventListener("click", () => rankingModal.classList.add("hidden"));
  rankingModal.addEventListener("click", (e) => { if (e.target === rankingModal) rankingModal.classList.add("hidden"); });

  keyboardCard.addEventListener("click", () => {
    keyboardModal.classList.remove("hidden");
    renderKeyboard(keyboardReferenceBoard, { mode: "reference" });
  });
  btnCloseKeyboard.addEventListener("click", () => keyboardModal.classList.add("hidden"));
  keyboardModal.addEventListener("click", (e) => { if (e.target === keyboardModal) keyboardModal.classList.add("hidden"); });

  async function handleStart() {
    const name = nameInput.value.trim();
    if (!name) {
      nameInput.classList.add("error");
      nameInput.focus();
      return;
    }
    if (starting) return; // bloqueio de início duplo
    nameInput.classList.remove("error");
    starting = true;
    btnStart.disabled = true;
    btnStart.textContent = "CARREGANDO...";

    try {
      const exercises = await apiFetchCampaign(selectedModule);
      const player = createPlayer(name);
      saveSession({ player, exercises, exerciseIndex: 0, resultData: null });
      window.location.href = "jogo.html";
    } catch {
      alert("Não foi possível carregar os exercícios. Verifique se o servidor está rodando.");
      starting = false;
      btnStart.disabled = false;
      btnStart.textContent = "INICIAR";
    }
  }

  btnStart.addEventListener("click", handleStart);
  nameInput.addEventListener("keydown", (e) => { if (e.key === "Enter") handleStart(); });

  // Autosave: oferece continuar de onde parou.
  const saved = loadAutosave();
  if (saved && saved.player && saved.exercises && saved.exercises.length) {
    resumeBanner.classList.remove("hidden");
    btnResume.addEventListener("click", () => {
      sessionStorage.setItem(SESSION_KEY, JSON.stringify(saved));
      window.location.href = saved.progressionPending ? PROGRESSION_PAGE : saved.resultData ? "resultado.html" : "jogo.html";
    });
    btnDiscardResume.addEventListener("click", () => {
      clearAutosave();
      resumeBanner.classList.add("hidden");
    });
  }
}

/* ============================================================
   8. PÁGINA: JOGO (jogo.html)
   ============================================================ */

function initGame() {
  applyTheme(getTheme());

  const state = loadSession();
  if (!state || !state.exercises || !state.exercises.length) {
    window.location.href = "index.html";
    return;
  }

  let { player, exercises, exerciseIndex } = state;
  let current = exercises[exerciseIndex];
  const isLastOfModule = current.indexInModule === current.totalInModule;

  const els = {
    playerName: document.getElementById("player-name"),
    hearts: document.getElementById("hearts"),
    scoreText: document.getElementById("score-text"),
    timerText: document.getElementById("timer-text"),
    timeProgress: document.getElementById("time-progress"),
    timeProgressFill: document.getElementById("time-progress-fill"),
    btnPause: document.getElementById("btn-pause"),
    moduleLabelLeft: document.getElementById("module-label-left"),
    moduleLabelRight: document.getElementById("module-label-right"),
    exerciseText: document.getElementById("exercise-text"),
    feedback: document.getElementById("feedback"),
    keyboardContainer: document.getElementById("keyboard-container"),
    input: document.getElementById("game-input"),
    sideStreak: document.getElementById("side-streak"),
    btnExit: document.getElementById("btn-exit"),
  };

  let typed = "";
  let timeLeft = current.timeLimit;
  let active = true;
  let paused = false;
  let startTime = Date.now();
  let countdownInterval = null;
  let leavingIntentionally = false; // true quando a própria página navega (não é o usuário fechando a aba)

  const avatarButton = document.getElementById("profile-avatar");
  const avatars = [
    { id: "profile-1", label: "perfil 1" },
    { id: "profile-2", label: "perfil 2" },
    { id: "profile-3", label: "perfil 3" },
    { id: "profile-4", label: "perfil 4" },
  ];
  const avatarStorageKey = `digita-comigo-avatar:${player.name.trim().toLocaleLowerCase("pt-BR")}`;
  let avatarIndex = 0;
  try {
    const savedAvatar = localStorage.getItem(avatarStorageKey);
    avatarIndex = Math.max(0, avatars.findIndex((avatar) => avatar.id === savedAvatar));
  } catch { /* Usa o primeiro perfil quando o armazenamento não estiver disponível. */ }
  function renderAvatar() {
    const avatar = avatars[avatarIndex];
    avatarButton.dataset.avatar = avatar.id;
    avatarButton.setAttribute("aria-label", `Avatar: ${avatar.label}. Trocar foto de perfil (${avatarIndex + 1} de ${avatars.length})`);
    avatarButton.title = `Foto ${avatarIndex + 1} de ${avatars.length}: ${avatar.label}. Clique para trocar`;
  }
  avatarButton.addEventListener("click", () => {
    avatarIndex = (avatarIndex + 1) % avatars.length;
    renderAvatar();
    try { localStorage.setItem(avatarStorageKey, avatars[avatarIndex].id); } catch { /* Mantém a escolha nesta página. */ }
  });
  renderAvatar();

  function renderTopBar() {
    els.playerName.textContent = player.name;
    els.hearts.textContent = heartsString(player.lives);
    els.scoreText.textContent = `${player.totalScore} pts`;
    const elapsedPercent = Math.max(0, Math.min(100, (1 - timeLeft / current.timeLimit) * 100));
    const remainingPercent = 100 - elapsedPercent;
    const timerColor = elapsedPercent <= 50 ? "#22c55e" : elapsedPercent <= 90 ? "#facc15" : "#ef4444";
    els.timerText.textContent = `${timeLeft}s`;
    els.timerText.style.color = timerColor;
    els.timeProgressFill.style.width = `${remainingPercent}%`;
    els.timeProgressFill.style.backgroundColor = timerColor;
    els.timeProgress.setAttribute("aria-valuenow", String(Math.round(remainingPercent)));
    els.timeProgress.setAttribute("aria-valuetext", `${timeLeft} segundos restantes`);
  }

  function renderModuleStepper() {
    els.moduleLabelLeft.textContent = `Módulo ${current.moduleNumber} de ${MODULES.length} — ${current.category}`;
    els.moduleLabelRight.textContent = `Frase ${current.indexInModule} de ${current.totalInModule}`;
  }

  function renderSidebar() {
    els.sideStreak.textContent = `${player.streak}x`;
  }

  function renderExerciseText(animatedIndex = -1) {
    const target = current.targetText;
    // Preserve as letras para não interromper animações durante a digitação rápida.
    if (els.exerciseText.textContent !== target) {
      els.exerciseText.replaceChildren(...Array.from({ length: target.length }, (_, i) => {
        const span = document.createElement("span");
        span.textContent = target[i];
        span.addEventListener("animationend", () => span.classList.remove("letter-pop", "letter-shake"));
        return span;
      }));
    }
    for (let i = 0; i < target.length; i++) {
      const ch = target[i];
      const span = els.exerciseText.children[i];
      span.classList.remove("correct", "wrong", "cursor", "normal");
      span.classList.add(i < typed.length ? (typed[i] === ch ? "correct" : "wrong") : i === typed.length ? "cursor" : "normal");
      if (i === animatedIndex && !window.matchMedia("(prefers-reduced-motion: reduce)").matches) {
        span.classList.remove("letter-pop", "letter-shake");
        void span.offsetWidth;
        span.classList.add(typed[i] === ch ? "letter-pop" : "letter-shake");
      }
    }
  }

  function setFeedback(msg, color) {
    els.feedback.textContent = msg;
    els.feedback.style.color = color;
  }

  function updateKeyboard(expected, vowelHint, hasError) {
    const states = computeKeyStates({ expected, vowelHint, hasError });
    renderKeyboard(els.keyboardContainer, { mode: "game", states });
  }

  let exerciseRecorded = false;
  async function confirmExit() {
    if (leavingIntentionally) return;
    if (window.confirm("Deseja encerrar a partida e ver seu resultado?")) {
      leavingIntentionally = true;
      active = false;
      stopCountdown();
      els.input.disabled = true;
      playExit();
      const elapsedSeconds = Math.max(0, current.timeLimit - timeLeft);
      const correctChars = countCorrect(typed, current.targetText);
      if (!exerciseRecorded) {
        recordSessionStats(player, { seconds: elapsedSeconds, typed, target: current.targetText });
        exerciseRecorded = true;
      }
      const finalState = {
        player, exercises, exerciseIndex, sessionEnded: true,
        resultData: {
          stars: 0, xp: 0,
          timeSeconds: elapsedSeconds,
          wpm: elapsedSeconds > 0 ? Math.round((correctChars / 5) / (elapsedSeconds / 60)) : 0,
        },
      };
      saveSession(finalState);
      await saveResultIfAny(player);
      saveSession({ ...finalState, rankingSubmitted: true });
      window.location.href = "resultado.html";
    }
  }
  els.btnExit.addEventListener("click", confirmExit);

  function togglePause() {
    if (!active) return;
    paused = !paused;
    els.btnPause.textContent = paused ? "Continuar" : "Pausar";
    if (paused) {
      setFeedback("JOGO PAUSADO", "var(--color-warning)");
      els.input.disabled = true;
    } else {
      setFeedback(" ", "var(--color-success)");
      els.input.disabled = false;
      els.input.focus();
    }
  }
  els.btnPause.addEventListener("click", togglePause);

  function stopCountdown() { if (countdownInterval) { clearInterval(countdownInterval); countdownInterval = null; } }

  function startCountdown() {
    stopCountdown();
    countdownInterval = setInterval(() => {
      if (paused) return;
      timeLeft--;
      renderTopBar();
      if (timeLeft <= 0) {
        stopCountdown();
        onTimeUp();
      }
    }, 1000);
  }

  function finishExercise(msg, color, stars, xp, wpm, timeSeconds) {
    active = false;
    stopCountdown();
    els.input.disabled = true;
    setFeedback(msg, color);
    playExerciseResult(stars);
    if (isLastOfModule) setTimeout(() => playModuleComplete(), 350);

    if (stars === 0) loseLife(player);
    else { addXP(player, xp); completeExercise(player); }

    recordSessionStats(player, {
      seconds: Math.max(0, current.timeLimit - timeLeft), typed, target: current.targetText,
      stars, completed: true, moduleCompleted: isLastOfModule && !isGameOver(player),
    });
    exerciseRecorded = true;

    renderTopBar();

    const resultData = { stars, xp, wpm, timeSeconds, description: current.description, category: current.category };
    setTimeout(async () => {
      if (leavingIntentionally) return;
      leavingIntentionally = true;
      if (isGameOver(player) || exerciseIndex + 1 >= exercises.length) {
        const finalState = { player, exercises, exerciseIndex, resultData, sessionEnded: true,
          endReason: isGameOver(player) ? "lives" : "completed" };
        saveSession(finalState);
        await saveResultIfAny(player);
        saveSession({ ...finalState, rankingSubmitted: true });
        window.location.href = "resultado.html";
        return;
      }
      if (showProgression({ player, exercises, exerciseIndex, resultData })) return;
      saveSession({ player, exercises, exerciseIndex: exerciseIndex + 1, resultData: null });
      window.location.href = "jogo.html";
    }, 900);
  }

  function onTimeUp() {
    if (!active) return;
    const elapsed = Date.now() - startTime;
    finishExercise("Tempo esgotado! Não desanime!", "var(--color-danger)", 0, 0, 0, Math.floor(elapsed / 1000));
  }

  function checkCompletion(typedFinal) {
    if (!active) return;
    active = false;
    stopCountdown();

    const target = current.targetText;
    const elapsed = Date.now() - startTime;
    const correct = countCorrect(typedFinal, target);
    const acc = correct / target.length;
    const stars = calculateStars(acc, elapsed, current.timeLimit);
    const xp = calculateScore(correct, target.length, elapsed, current.xpReward, current.timeLimit);
    const timeSeconds = Math.floor(elapsed / 1000);
    const words = target.trim().split(/\s+/).filter(Boolean).length;
    const minutes = Math.max(elapsed / 60000, 1 / 60);
    const wpm = Math.round(words / minutes);

    let msg, color;
    if (stars === 3) { msg = pickRandom(MSGS_3_STARS); color = "var(--color-success)"; }
    else if (stars === 2) { msg = pickRandom(MSGS_2_STARS); color = "var(--color-success-2)"; }
    else if (stars === 1) { msg = pickRandom(MSGS_1_STAR); color = "var(--color-warning)"; }
    else { msg = pickRandom(MSGS_0_STARS); color = "var(--color-danger)"; }

    finishExercise(msg, color, stars, xp, wpm, timeSeconds);
  }

  function handleInput() {
    if (!active || paused) return;
    let value = els.input.value;
    const target = current.targetText;
    if (value.length > target.length) {
      value = value.slice(0, target.length);
      els.input.value = value;
    }
    const grew = value.length > typed.length;
    const shrank = value.length < typed.length;
    let animatedIndex = -1;
    if (!shrank) {
      for (let i = 0; i < value.length; i++) {
        if (value[i] !== typed[i]) animatedIndex = i;
      }
    }
    typed = value;
    renderExerciseText(animatedIndex);

    if (shrank) playErase();

    if (value.length > 0) {
      const last = value[value.length - 1];
      const expected = target[value.length - 1];
      const correct = last === expected;
      setFeedback(correct ? pickRandom(MSGS_CORRECT) : pickRandom(MSGS_WRONG), correct ? "var(--color-success)" : "var(--color-danger)");
      if (grew) correct ? playCorrect() : playWrong();

      if (!correct) {
        updateKeyboard(null, null, true);
      } else if (value.length < target.length) {
        const nextCh = target[value.length];
        updateKeyboard(nextCh, baseVowel(nextCh), false);
      }
    } else {
      setFeedback(" ", "var(--color-success)");
      if (target) updateKeyboard(target[0], baseVowel(target[0]), false);
    }

    if (value.length === target.length) checkCompletion(value);
  }
  els.input.addEventListener("input", handleInput);

  function loadExercise() {
    current = exercises[exerciseIndex];
    typed = "";
    els.input.value = "";
    timeLeft = current.timeLimit;
    active = true;
    paused = false;
    els.btnPause.textContent = "Pausar";
    els.input.disabled = false;
    startTime = Date.now();
    setFeedback(" ", "var(--color-success)");

    renderTopBar();
    renderModuleStepper();
    renderSidebar();
    renderExerciseText();

    if (current.targetText) updateKeyboard(current.targetText[0], baseVowel(current.targetText[0]), false);

    startCountdown();
    els.input.focus();
  }

  // Esc sai da partida (com confirmação).
  document.addEventListener("keydown", (e) => { if (e.key === "Escape") confirmExit(); });

  // Confirmação nativa ao tentar fechar a aba — só quando NÃO é a própria página
  // navegando (fim de exercício, saída pelo botão/Esc já confirmados acima).
  window.addEventListener("beforeunload", (e) => {
    if (leavingIntentionally) return;
    e.preventDefault();
    e.returnValue = "";
  });

  loadExercise();
}

/* ============================================================
   9. PÁGINA: RESULTADO (resultado.html)
   ============================================================ */

const AUTO_ADVANCE_SECONDS = 5;

function initResult() {
  applyTheme(getTheme());
  const state = loadSession();
  if (!state?.player || !state.resultData) {
    window.location.href = "index.html";
    return;
  }
  // Retoma estados antigos sem apresentar um resultado intermediario.
  if (!state.sessionEnded && !isGameOver(state.player) && state.exerciseIndex + 1 < state.exercises.length) {
    if (showProgression(state)) return;
    saveSession({ ...state, exerciseIndex: state.exerciseIndex + 1, resultData: null });
    window.location.href = "jogo.html";
    return;
  }
  const { player } = state;
  const stats = player.sessionStats;
  const set = (id, value) => { document.getElementById(id).textContent = value; };
  const seconds = stats ? stats.seconds : null;
  set("stat-lives", heartsString(player.lives));
  set("stat-score", player.totalScore + " pts");
  set("stat-streak", player.streak + "x");
  set("stat-time", seconds === null ? "?" : Math.floor(seconds / 60) + ":" + String(Math.floor(seconds % 60)).padStart(2, "0"));
  set("stat-wpm", seconds > 0 ? Math.round((stats.correct / 5) / (seconds / 60)) + " ppm" : "?");
  set("stat-completed", player.exercisesCompleted);
  set("stat-accuracy", stats?.characters ? Math.round(stats.correct / stats.characters * 100) + "%" : "?");
  set("stat-best-streak", stats ? stats.bestStreak + "x" : "?");
  set("stat-stars", stats ? stats.stars : "?");
  set("stat-modules", stats ? stats.modules : "?");
  set("stat-characters", stats ? stats.correct + " / " + stats.characters : "?");
  const message = state.endReason === "completed"
    ? "Parabéns, " + player.name + "! Você concluiu o módulo de Pontuação e finalizou sua jornada!"
    : isGameOver(player) ? "Suas vidas acabaram, mas cada prática conta. Confira seu progresso!"
    : "Muito bem, " + player.name + "! Confira o resultado da sua sessão.";
  set("result-message", message);
  set("auto-advance-hint", "Enter ou Voltar ao menu para encerrar. Tempo e PPM consideram a prática, sem as pausas. A precisão considera o texto que ficou digitado.");
  let finishing = false;
  async function finish() {
    if (finishing) return;
    finishing = true;
    if (!state.rankingSubmitted) await saveResultIfAny(player);
    clearSession();
    window.location.href = "index.html";
  }
  document.getElementById("btn-finish").addEventListener("click", finish);
  document.addEventListener("keydown", (event) => {
    if (event.key === "Enter" || event.key === "Escape") { event.preventDefault(); finish(); }
  });
}

/* ============================================================
   10. PÁGINA: PROGRESSÃO ENTRE MÓDULOS
   ============================================================ */

function initProgression() {
  applyTheme(getTheme());
  const state = loadSession();
  const nextModule = getUpcomingModule(state);
  if (!state?.progressionPending || !nextModule) {
    window.location.replace(state?.resultData ? "resultado.html" : state?.player ? "jogo.html" : "index.html");
    return;
  }

  const lesson = MODULE_LESSONS[nextModule.moduleNumber];
  document.getElementById("progression-player").textContent = `Parabéns, ${state.player.name}!`;
  document.getElementById("progression-title").textContent = `VOCÊ DESBLOQUEOU O ${lesson.ordinal} MÓDULO!`;
  document.getElementById("progression-completed").textContent = `Módulo ${state.exercises[state.exerciseIndex].moduleNumber} concluído: 8 frases praticadas. Mais um passo na sua jornada!`;
  document.getElementById("progression-icon").textContent = nextModule.icon;
  document.getElementById("progression-learning").textContent = `O módulo ${nextModule.moduleNumber} traz: ${lesson.title}`;
  document.getElementById("progression-description").textContent = lesson.description;
  document.getElementById("progression-tip").textContent = lesson.tip;

  const button = document.getElementById("btn-progression-continue");
  const hint = document.getElementById("progression-countdown");
  let advancing = false;
  let countdown = AUTO_ADVANCE_SECONDS;
  let interval;

  function handleContinue() {
    if (advancing) return;
    advancing = true;
    clearInterval(interval);
    button.disabled = true;
    saveSession({ player: state.player, exercises: state.exercises, exerciseIndex: state.exerciseIndex + 1, resultData: null });
    window.location.href = "jogo.html";
  }

  function renderCountdown() {
    hint.textContent = `Continuando automaticamente em ${countdown}s — ou pressione Enter`;
  }

  button.addEventListener("click", handleContinue);
  document.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && !event.repeat) {
      event.preventDefault();
      handleContinue();
    }
  });
  renderCountdown();
  interval = setInterval(() => {
    countdown--;
    if (countdown <= 0) handleContinue();
    else renderCountdown();
  }, 1000);
  window.addEventListener("pagehide", () => clearInterval(interval));
  button.focus();
}

/* ============================================================
   11. DISPATCH — decide qual página inicializar
   ============================================================ */

function fitPageToViewport() {
  const page = document.querySelector(".page");
  const content = page?.querySelector(":scope > .page-content");
  if (!content) return;
  const footer = page.querySelector(":scope > .menu-footer");

  // Mantém a margem fora da escala: somente os elementos internos diminuem.
  const initialPadding = parseFloat(getComputedStyle(content).paddingLeft) || 16;
  page.classList.add("fitted-page");
  let frame = null;

  function fit() {
    frame = null;
    const viewport = window.visualViewport;
    const width = viewport?.width || window.innerWidth;
    const height = viewport?.height || window.innerHeight;
    const contentHeight = Math.max(1, height - (footer?.offsetHeight || 0));
    const sideMargin = Math.min(40, Math.max(16, width * 0.028));
    const verticalMargin = Math.min(initialPadding, height * 0.05);
    const availableWidth = Math.max(1, width - sideMargin * 2);
    const availableHeight = Math.max(1, contentHeight - verticalMargin * 2);
    content.style.transform = "none";
    let layoutWidth = availableWidth;
    let naturalWidth, naturalHeight, scale;
    // Compensa a largura perdida na redução, evitando grandes faixas vazias
    // nas laterais. A largura adicional também reduz as quebras dos textos.
    for (let pass = 0; pass < 8; pass++) {
      content.style.width = `${layoutWidth}px`;
      naturalWidth = Math.max(content.offsetWidth, content.scrollWidth);
      naturalHeight = Math.max(content.offsetHeight, content.scrollHeight);
      scale = Math.min(1, availableWidth / naturalWidth, availableHeight / naturalHeight);
      const targetWidth = availableWidth / Math.min(1, availableHeight / naturalHeight);
      if (Math.abs(targetWidth - layoutWidth) < 1) break;
      layoutWidth = targetWidth;
    }
    content.style.transform = `scale(${scale})`;
    content.style.left = `${(width - naturalWidth * scale) / 2}px`;
    content.style.top = `${(contentHeight - naturalHeight * scale) / 2}px`;
  }

  function scheduleFit() {
    if (frame === null) frame = requestAnimationFrame(fit);
  }

  new ResizeObserver(scheduleFit).observe(content);
  if (footer) new ResizeObserver(scheduleFit).observe(footer);
  // Ranking, autosave e textos dos exercícios podem mudar após a primeira medição.
  new MutationObserver(scheduleFit).observe(content, { childList: true, subtree: true, characterData: true });
  window.addEventListener("resize", scheduleFit);
  window.visualViewport?.addEventListener("resize", scheduleFit);
  content.querySelectorAll("img").forEach((img) => img.addEventListener("load", scheduleFit));
  document.fonts?.ready.then(scheduleFit);
  fit();
}

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.dataset.page;
  if (page === "menu") initMenu();
  else if (page === "game") initGame();
  else if (page === "result") initResult();
  else if (page === "progression") initProgression();
  initThemeControl();
  fitPageToViewport();
});
