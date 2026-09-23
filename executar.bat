@echo off
chcp 65001 >nul
title Digita Comigo - Site
cd /d "%~dp0"

echo Verificando Node.js...
where node >nul 2>nul
if errorlevel 1 (
    echo.
    echo Node.js nao encontrado. Instale em https://nodejs.org antes de continuar.
    pause
    exit /b 1
)

node -e "require('node:sqlite')" >nul 2>nul
if errorlevel 1 (
    echo Este jogo precisa de uma versao do Node.js com suporte a node:sqlite.
    echo Atualize o Node.js para a versao 22.13 ou superior.
    pause
    exit /b 1
)

if not exist backend\node_modules (
    echo Instalando dependencias do servidor...
    pushd backend
    call npm install
    if errorlevel 1 (
        echo Nao foi possivel instalar as dependencias.
        popd
        pause
        exit /b 1
    )
    popd
)

echo.
echo Iniciando o servidor...
set "PORT=3001"
pushd "%~dp0backend"
start "Digita Comigo - Servidor" cmd /k node src/server.js
popd

echo Aguardando o servidor subir...
node -e "let tries=0; async function check(){try{const r=await fetch('http://localhost:3001/api/health',{signal:AbortSignal.timeout(1000)});if(r.ok&&(await r.json()).ok)process.exit(0);}catch{}if(++tries>=30)process.exit(1);setTimeout(check,500);}check();"
if errorlevel 1 (
    echo O servidor nao iniciou. Confira o erro na janela Digita Comigo - Servidor.
    pause
    exit /b 1
)

echo Abrindo o jogo no navegador...
start http://localhost:3001

echo.
echo Tudo pronto! Uma janela de terminal foi aberta - deixe ela rodando.
echo Para parar o jogo, feche essa janela.
pause
