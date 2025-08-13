import { useState, useMemo } from "react";
import api from "./util/axios";

export default function App() {
  const [codeLength, setCodeLength] = useState(4);
  const [maxDigit, setMaxDigit] = useState(7);
  const [maxAttempts, setMaxAttempts] = useState(10);
  const [maxHints, setMaxHints] = useState(2);

  const [gameId, setGameId] = useState(null);
  const [status, setStatus] = useState("IDLE");
  const [attempt, setAttempt] = useState(0);
  const [guessInput, setGuessInput] = useState("");
  const [history, setHistory] = useState([]);
  const [hintsUsed, setHintsUsed] = useState(0);
  const [revealed, setRevealed] = useState({});
  const [existingGameIdInput, setExistingGameIdInput] = useState("");

  const attemptsLeft = Math.max(0, maxAttempts - attempt);
  const hintsLeft = Math.max(0, maxHints - hintsUsed);

  const displayCodeSlots = useMemo(
    () => Array.from({ length: codeLength }),
    [codeLength]
  );

  const mainGridCols = gameId ? "lg:grid-cols-3" : "lg:grid-cols-1";

  const onCreateGame = async () => {
    const res = await api.post("/game", {
      codeLength,
      maxDigit,
      maxAttempts,
      maxHints,
    });

    const data = res.data;
    setGameId(data.id);
    setHistory(data.history);
    setStatus(data.status);
    setAttempt(data.attempts);
    setRevealed(data.hints);
    setHintsUsed(data.hintsUsed);
  };

  const onSubmitGuess = async (e) => {
    e.preventDefault();
    const guessArr = guessInput.split(" ").map((x) => Number(x));
    console.log(guessArr);

    const res = await api.post("/game/guess", { gameId, guess: guessArr });
    const data = res.data;
    console.log(res.data);
    // update game state
    setHistory(data.history);
    setAttempt(data.attempts);
    setStatus(data.status);
    setGuessInput("");

    if (data.status == "WON") {
      setRevealed(() => {
        const obj = {};
        guessArr.forEach((guess, index) => {
          obj[index] = guess;
        });

        return obj;
      });
    } else if (data.status == "LOST") {
      // fetch secret code and reveal it to user
    }
  };

  const onRequestHint = async () => {
    const res = await api.get(`/game/hint/${gameId}`);
    const data = res.data;

    setHintsUsed((prev) => prev + 1);
    setRevealed((prev) => ({
      ...prev,
      [data.location]: data.digit,
    }));
  };

  const onLoadGameById = async () => {
    const res = await api.get(`/game/${existingGameIdInput}`);
    const data = res.data;

    console.log(data);

    // set game state
    setGameId(data.id);
    setHistory(data.history);
    setStatus(data.status);
    setAttempt(data.attempts);
    setHintsUsed(data.hintsUsed);
    setRevealed(() => {
      let rev = {};
      data.hints.forEach((hint) => {
        rev = {
          ...rev,
          [hint.location]: hint.digit,
        };
      });

      return rev;
    });

    setExistingGameIdInput("");
  };

  return (
    <div className="min-h-screen bg-gray-50 text-gray-900">
      <header className="sticky top-0 z-10 backdrop-blur supports-[backdrop-filter]:bg-white/70 bg-white border-b">
        <div className="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
          <h1 className="text-2xl font-semibold tracking-tight">Mastermind</h1>
          <div className="flex items-center gap-2">
            <span
              className={`text-sm px-2 py-1 rounded-full border ${
                status === "IN_PROGRESS"
                  ? "bg-emerald-50 border-emerald-200 text-emerald-700"
                  : status === "WON"
                  ? "bg-indigo-50 border-indigo-200 text-indigo-700"
                  : status === "LOST"
                  ? "bg-rose-50 border-rose-200 text-rose-700"
                  : "bg-gray-50 border-gray-200 text-gray-600"
              }`}
            >
              {status}
            </span>
          </div>
        </div>
      </header>

      <main
        className={`max-w-5xl mx-auto px-4 py-6 grid gap-6 ${mainGridCols}`}
      >
        <section className="lg:col-span-1">
          <div className="bg-white rounded-2xl shadow-sm border p-5">
            <h2 className="text-lg font-semibold mb-4">Game Settings</h2>

            <div className="space-y-5">
              <div>
                <label className="block text-sm font-medium mb-1">
                  Secret code length
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="range"
                    min={4}
                    max={10}
                    value={codeLength}
                    onChange={(e) => setCodeLength(parseInt(e.target.value))}
                    className="w-full"
                  />
                  <input
                    type="number"
                    min={4}
                    max={10}
                    value={codeLength}
                    onChange={(e) => setCodeLength(Number(e.target.value))}
                    className="w-20 px-2 py-1.5 rounded-md border"
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  Choose between 4 and 10 digits.
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Maximum digit allowed
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="range"
                    min={1}
                    max={9}
                    value={maxDigit}
                    onChange={(e) => setMaxDigit(parseInt(e.target.value))}
                    className="w-full"
                  />
                  <input
                    type="number"
                    min={1}
                    max={9}
                    value={maxDigit}
                    onChange={(e) => setMaxDigit(Number(e.target.value))}
                    className="w-20 px-2 py-1.5 rounded-md border"
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  Digits will range from 0 to your selected maximum.
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Maximum attempts
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="range"
                    min={1}
                    max={20}
                    value={maxAttempts}
                    onChange={(e) => setMaxAttempts(parseInt(e.target.value))}
                    className="w-full"
                  />
                  <input
                    type="number"
                    min={1}
                    max={20}
                    value={maxAttempts}
                    onChange={(e) => setMaxAttempts(Number(e.target.value))}
                    className="w-20 px-2 py-1.5 rounded-md border"
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  How many guesses the player gets.
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Hints per game
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="range"
                    min={0}
                    max={codeLength}
                    value={Math.min(maxHints, codeLength)}
                    onChange={(e) => setMaxHints(parseInt(e.target.value))}
                    className="w-full"
                  />
                  <input
                    type="number"
                    min={0}
                    max={codeLength}
                    value={Math.min(maxHints, codeLength)}
                    onChange={(e) => setMaxHints(Number(e.target.value))}
                    className="w-20 px-2 py-1.5 rounded-md border"
                  />
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  You decide the cap. Must be less than code length.
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Load existing game
                </label>
                <div className="flex items-center gap-3">
                  <input
                    type="text"
                    inputMode="numeric"
                    placeholder="Enter game ID"
                    value={existingGameIdInput}
                    onChange={(e) => setExistingGameIdInput(e.target.value)}
                    className="flex-1 px-3 py-2 rounded-xl border"
                  />
                  <button
                    type="button"
                    onClick={onLoadGameById}
                    className="px-4 py-2 rounded-xl border hover:bg-gray-50"
                  >
                    Load
                  </button>
                </div>
              </div>

              <button
                onClick={onCreateGame}
                className="w-full mt-2 rounded-xl bg-gray-900 text-white py-2.5 hover:bg-black transition"
              >
                Start Game
              </button>
            </div>
          </div>
        </section>

        {gameId && (
          <section className="lg:col-span-2">
            <div className="bg-white rounded-2xl shadow-sm border p-5">
              <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
                <h2 className="text-lg font-semibold">Play</h2>
                <div className="flex items-center gap-2 text-sm">
                  <span className="px-2 py-1 rounded-md border bg-gray-50">
                    Game ID: {gameId ?? "—"}
                  </span>
                  <span className="px-2 py-1 rounded-md border bg-gray-50">
                    Attempt: {attempt} / {maxAttempts}
                  </span>
                  <span className="px-2 py-1 rounded-md border bg-gray-50">
                    Hints: {hintsUsed} / {maxHints}
                  </span>
                </div>
              </div>

              <form onSubmit={onSubmitGuess} className="grid gap-4">
                <div>
                  <label className="block text-sm font-medium mb-1">
                    Enter your guess
                  </label>
                  <div className="flex items-center gap-3">
                    <input
                      value={guessInput}
                      onChange={(e) => setGuessInput(e.target.value)}
                      placeholder={`e.g. 1 2 3 4 (0–${maxDigit})`}
                      className="flex-1 px-3 py-2 rounded-xl border focus:outline-none focus:ring-2 focus:ring-indigo-200"
                    />
                    <button
                      type="submit"
                      className="px-4 py-2 rounded-xl bg-indigo-600 text-white hover:bg-indigo-700"
                    >
                      Guess
                    </button>
                    <button
                      type="button"
                      onClick={onRequestHint}
                      disabled={hintsLeft <= 0 || status !== "IN_PROGRESS"}
                      className="px-4 py-2 rounded-xl border hover:bg-gray-50 disabled:opacity-50"
                    >
                      Hint
                    </button>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Use spaces to separate the digits. Length must equal{" "}
                    {codeLength}.
                  </p>
                  <p className="text-xs text-gray-500">e.g. "1 2 3 4"</p>
                </div>

                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-2">
                  {displayCodeSlots.map((_, i) => (
                    <div
                      key={i}
                      className="aspect-square rounded-xl border grid place-items-center text-lg bg-gray-50"
                      title={`Position ${i + 1}`}
                    >
                      {revealed[i] ?? "•"}
                    </div>
                  ))}
                </div>
              </form>

              <div className="mt-6">
                <h3 className="text-sm font-semibold text-gray-700 mb-2">
                  History
                </h3>
                {history.length === 0 ? (
                  <p className="text-sm text-gray-500">
                    No guesses yet. Your past guesses will appear here.
                  </p>
                ) : (
                  <ul className="space-y-2">
                    {history.map((h, idx) => (
                      <li
                        key={idx}
                        className="flex items-center justify-between rounded-xl border p-2"
                      >
                        <div className="flex items-center gap-2">
                          <span className="text-xs text-gray-500">
                            #{idx + 1}
                          </span>
                          <span className="font-mono text-sm">
                            {Array.isArray(h.guess)
                              ? h.guess.join(" ")
                              : String(h.guess)}
                          </span>
                        </div>
                        <div className="flex items-center gap-2 text-xs">
                          <span className="px-2 py-1 rounded-md bg-emerald-50 border border-emerald-200 text-emerald-700">
                            Correct digits: {h.result.correctNumbers}
                          </span>
                          <span className="px-2 py-1 rounded-md bg-amber-50 border border-amber-200 text-amber-800">
                            Correct locations: {h.result.correctLocations}
                          </span>
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </section>
        )}
      </main>

      {gameId && (
        <footer className="max-w-5xl mx-auto px-4 pb-10">
          <div className="bg-white rounded-2xl shadow-sm border p-5 flex flex-wrap items-center justify-between gap-3">
            <div className="text-sm text-gray-600">
              Attempts left: {attemptsLeft} • Hints left: {hintsLeft}
            </div>
            <div className="flex gap-2">
              <button
                onClick={onCreateGame}
                className="px-4 py-2 rounded-xl border hover:bg-gray-50"
              >
                New Game
              </button>
            </div>
          </div>
        </footer>
      )}
    </div>
  );
}
