#!/bin/bash

APP_DIR="/home/ec2-user/app"
JAR_FILE="$APP_DIR/app.jar"
LOG_FILE="$APP_DIR/app.log"

echo ">>> Stop existing application"
PID=$(pgrep -f "$JAR_FILE")
if [ -n "$PID" ]; then
  echo ">>> Killing process: $PID"
  kill $PID || true
  sleep 5

  if ps -p $PID > /dev/null; then
    echo ">>> Process $PID still running. Forcing termination."
    kill -9 $PID || true
  fi
else
  echo ">>> No running application found."
fi

echo ">>> Start new application"
nohup java -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
echo ">>> Application started with PID: $(pgrep -f "$JAR_FILE")"
