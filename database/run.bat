@echo off
chcp 65001
cd /d "%~dp0"
mongodb\bin\mongod --config ./mongod.conf