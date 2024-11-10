#!/bin/bash

# 📚 =================================================================
# 🚀 QUICK GUIDE FOR SCRIPT USAGE
# =================================================================
#
# 1. 🔧 PREPARATION (first time only)
# ----------------------------------------------
#   chmod +x start-container.sh
#
# 2. 🏃 START AND ACCESS THE CONTAINER
# ----------------------------------------------
# 📋 Standard start with interactive menu:
#   ./start-container.sh
#
# 🖥️ Direct shell access (without menu):
#   ./start-container.sh -s
#   ./start-container.sh --shell
#
# ⚡ Other available commands:
#   ./start-container.sh --rebuild    # Rebuilds the image
#   ./start-container.sh -r           # Restarts the container
#
# 3. 🛠️ USEFUL COMMANDS INSIDE CONTAINER
# ----------------------------------------------
# Once inside the container you can use:
# 📍 pwd            # Check current directory
# 📂 ls -la         # List files
# 🔄 ps aux         # List processes
# 🌍 env            # List environment variables
# 💾 df -h          # Check disk space
# 🚪 exit           # Exit shell
#
# 4. ℹ️ NOTES
# ----------------------------------------------
# 💡 With -s/--shell option, exiting the shell will keep
#    the container running
# 🛑 To stop the container after shell exit, use:
#    docker-compose down
# =================================================================

# 🔒 Security settings
set -euo pipefail
IFS=$'\n\t'

# 🎨 Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ⚙️ Configuration
CONTAINER_NAME="p4pa-io-notification"
DOCKER_COMPOSE_FILE="docker-compose.yml"
MAX_WAIT_TIME=60
DIRECT_SHELL=false

# 📝 Function for logging
log() {
   echo -e "${2:-$YELLOW}$(date '+%Y-%m-%d %H:%M:%S') - $1${NC}"
}

# 🔍 Function to check prerequisites
check_prerequisites() {
   log "Checking prerequisites..."
   
   if ! command -v docker &> /dev/null; then
       log "❌ Docker is not installed!" "$RED"
       exit 1
   fi
   
   if ! command -v docker-compose &> /dev/null; then
       log "❌ Docker Compose is not installed!" "$RED"
       exit 1
   fi
   
   if ! docker info &> /dev/null; then
       log "❌ Docker is not running!" "$RED"
       exit 1
   fi
   
   if [ ! -f "$DOCKER_COMPOSE_FILE" ]; then
       log "❌ File $DOCKER_COMPOSE_FILE not found!" "$RED"
       exit 1
   fi
}

# 🎯 Function to check container status
check_container() {
   # Check if container exists using inspect
   if ! docker container inspect "$CONTAINER_NAME" &>/dev/null; then
       echo "❌ ERROR: Container $CONTAINER_NAME does not exist" >&2
       return 1
   fi

   # Get container status
   status=$(docker container inspect -f '{{.State.Status}}' "$CONTAINER_NAME" 2>/dev/null)
   
   case "$status" in
       "running")
           echo "✅ Container $CONTAINER_NAME is running"
           return 0
           ;;
       "exited")
           echo "⏹️ Container $CONTAINER_NAME exists but is stopped" >&2
           return 2
           ;;
       "created"|"restarting"|"removing"|"paused"|"dead")
           echo "⚠️ Container $CONTAINER_NAME status: $status" >&2
           return 3
           ;;
       *)
           echo "❌ ERROR: Unknown container status: $status" >&2
           return 4
           ;;
   esac
}

# 🚀 Function to start the container
start_container() {
   log "🚀 Starting container $CONTAINER_NAME..."
   docker-compose up -d
   
   local waited=0
   while ! check_container && [ $waited -lt $MAX_WAIT_TIME ]; do
       sleep 1
       waited=$((waited + 1))
       echo -n "."
   done
   echo ""
   
   if [ $waited -ge $MAX_WAIT_TIME ]; then
       log "⏱️ Timeout while starting container!" "$RED"
       exit 1
   fi
   
   log "✨ Container started successfully" "$GREEN"
}

# 🖥️ Function to access container shell
enter_shell() {
   if check_container; then
       log "🔌 Accessing container shell..."
       docker exec -it $CONTAINER_NAME /bin/sh
       return $?
   else
       log "❌ Container is not running!" "$RED"
       return 1
   fi
}

# 📋 Main menu
show_menu() {
   echo -e "\n${YELLOW}=== Container Management Menu ===${NC}"
   echo "1) 🖥️  Access container shell"
   echo "2) 📜 View logs"
   echo "3) ℹ️  Container status"
   echo "4) 🔄 Restart container"
   echo "5) ⏹️  Stop container"
   echo "6) 🚪 Exit"
   echo -e "${YELLOW}===============================${NC}\n"
}

# 🎮 Main function
main() {
   check_prerequisites
   
   # 🎯 Parameter handling
   case "${1:-}" in
       -s|--shell)
           DIRECT_SHELL=true
           if ! check_container; then
               start_container
           fi
           enter_shell
           exit 0
           ;;
       -r|--restart)
           if check_container; then
               docker-compose down
           fi
           start_container
           ;;
       --rebuild)
           log "🔄 Rebuilding container..."
           docker-compose down
           docker-compose build --no-cache
           start_container
           ;;
       -h|--help)
           show_usage
           exit 0
           ;;
       "")
           if ! check_container; then
               start_container
           fi
           ;;
       *)
           log "❌ Invalid option: $1" "$RED"
           show_usage
           exit 1
           ;;
   esac
   
   # If not direct shell, enter normal cycle
   if [ "$DIRECT_SHELL" = false ]; then
       enter_shell
       
       # Post-shell menu
       while true; do
           show_menu
           read -p "Select an option (1-6): " choice
           
           case $choice in
               1) enter_shell ;;
               2) docker logs -f $CONTAINER_NAME ;;
               3) 
                   docker ps -f name=$CONTAINER_NAME
                   echo -e "\n${YELLOW}📊 Resource usage:${NC}"
                   docker stats --no-stream $CONTAINER_NAME
                   ;;
               4)
                   docker-compose restart
                   enter_shell
                   ;;
               5)
                   docker-compose down
                   exit 0
                   ;;
               6)
                   log "👋 Exiting. Container will continue running." "$GREEN"
                   exit 0
                   ;;
               *)
                   log "❌ Invalid option!" "$RED"
                   ;;
           esac
       done
   fi
}

# ⚡ Signal handling
trap 'echo -e "\n${RED}Script interrupted${NC}"; exit 1' INT TERM

# 🏃 Script execution
main "$@"
