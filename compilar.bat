@echo off
chcp 65001 >nul
echo Compilando DigitaComigo...
if not exist out mkdir out

javac -cp "lib\*" -d out -encoding UTF-8 ^
  Main.java ^
  model\Player.java ^
  model\Exercise.java ^
  model\LetterExercise.java ^
  model\WordExercise.java ^
  model\SentenceExercise.java ^
  factory\ExerciseFactory.java ^
  ui\TypingGame.java ^
  ui\panels\BasePanel.java ^
  ui\panels\KeyboardPanel.java ^
  ui\panels\MenuPanel.java ^
  ui\panels\GamePanel.java ^
  ui\panels\ResultPanel.java

if %errorlevel%==0 (
    echo.
    echo Compilacao concluida com sucesso!
    echo Para jogar, execute:  executar.bat
) else (
    echo.
    echo Erro na compilacao. Verifique o Java instalado com: java -version
)
pause