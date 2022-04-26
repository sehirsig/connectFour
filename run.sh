#!/bin/bash

RED='\033[31;1m'
YELLOW='\033[33;1m'
BOLD='\033[1m'
RESET='\033[0m'

welcome_string="Welcome to ${RED}connect${YELLOW}Four${RESET} by ${BOLD}@sehirsig${RESET} and ${BOLD}@furkankarayal${RESET}.
Be sure to enable '${BOLD}xhost +${RESET}' for GUI.

${RED}▄▄·        ▐ ▄  ▐ ▄ ▄▄▄ . ▄▄· ▄▄▄▄▄·${YELLOW}▄▄▄      ▄• ▄▌▄▄▄
${RED}▐█ ▌▪▪     •█▌▐█•█▌▐█▀▄.▀·▐█ ▌▪•██  ${YELLOW}▐▄▄·▪     █▪██▌▀▄ █·
${RED}██ ▄▄ ▄█▀▄ ▐█▐▐▌▐█▐▐▌▐▀▀▪▄██ ▄▄ ▐█.${YELLOW}▪██▪  ▄█▀▄ █▌▐█▌▐▀▀▄
${RED}▐███▌▐█▌.▐▌██▐█▌██▐█▌▐█▄▄▌▐███▌ ▐█▌·${YELLOW}██▌.▐█▌.▐▌▐█▄█▌▐█•█▌
${RED}·▀▀▀  ▀█▄▀▪▀▀ █▪▀▀ █▪ ▀▀▀ ·▀▀▀  ▀▀▀ ${YELLOW}▀▀▀  ▀█▄▀▪ ▀▀▀ .▀  ▀

${RESET}
"

printf "$welcome_string"

printf "What View do you want to select? (${BOLD}gui${RESET}/${BOLD}tui${RESET}): "
read selectview
SELVIEW=${selectview,,}

if [[ $SELVIEW == "gui" ]]; then
  export C4_UITYPE="gui"
elif [[ $SELVIEW == "tui" ]]; then
  export C4_UITYPE="tui"
elif [[ $SELVIEW == "q" ]]; then
  exit 1
else
  export C4_UITYPE="gui"
fi

printf "Do you want to enable REST VIEW API? (${BOLD}y${RESET}/${BOLD}n${RESET}): "
read selectrestview
SELRES=${selectrestview,,}

if [[ $SELRES == "n" ]]; then
  export C4_VIEWREST="n"
elif [[ $SELRES == "q" ]]; then
  exit 1
else
  export C4_VIEWREST="y"
fi

sbt "run"

exec nginx