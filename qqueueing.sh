#!/bin/bash
################################################################################
##################################PATH SETTING##################################
################################################################################
URL=k10a401.p.ssafy.io

################################################################################
################################################################################
################################################################################

usage() {
    printf "Usage: \n\t QQueueuing [command]"
    printf "\n\nAvailable Commands:"
    printf "\n\tstart\t\t\tStart all services"
    printf "\n\tstop\t\t\tStop all services"
    printf "\n\trestart\t\t\tRestart all stopped and running services"
    printf "\n\tlogs [-f]\t\tShow openvidu logs."
    printf "\n\tkms-logs [-f]\t\tShow kms logs"
    printf "\n\tupgrade\t\t\tUpgrade to the latest Openvidu version"
    printf "\n\tupgrade [version]\tUpgrade to the specific Openvidu version"
    printf "\n\tversion\t\t\tShow version of Openvidu Server"
    printf "\n\treport\t\t\tGenerate a report with the current status of Openvidu"
    printf "\n\thelp\t\t\tShow help for openvidu command"
    printf "\n"
}

# need to verify docker, compose exists
# need to check their version
# need to check user set files
case $1 in
  build)
	if [[ -z $2 ]];then
	  printf "\t no extra input"
	  printf "\t build all application"
	fi
	echo "build images to run containers"
	echo $PWD

	# front, main, con, pro, kaf, 
	;;

  

  start)
    docker-compose up -d
    if [[ "${FOLLOW_OPENVIDU_LOGS}" == "true" ]]; then
      docker-compose logs -f --tail 10 openvidu-server
    fi
    ;;

  stop)
    docker-compose down
    ;;

  restart)
    docker-compose down
    docker-compose up -d
    if [[ "${FOLLOW_OPENVIDU_LOGS}" == "true" ]]; then
      docker-compose logs -f --tail 10 openvidu-server
    fi
    ;;

  logs)
    case "${2-}" in
      --follow|-f)
        docker-compose logs -f --tail 10 openvidu-server
        ;;
      *)
        docker-compose logs openvidu-server
        ;;
    esac
    ;;

  kms-logs)
    kurento_logs "$2"
    ;;

  upgrade)
    if [ -z "$2" ]; then
      UPGRADE_VERSION="latest"
    else
      UPGRADE_VERSION="$2"
    fi

    read -r -p "  You're about to update OpenVidu CE to '${UPGRADE_VERSION}' version. Are you sure? [y/N]: " response
    case "$response" in
      [yY][eE][sS]|[yY])
        upgrade_ov "${UPGRADE_VERSION}"
        ;;
      *)
        exit 0
        ;;
    esac
    ;;

  version)
    version_ov
    ;;

  report)
    read -r -p "  You are about to generate a report on the current status of Openvidu, this may take some time. Do you want to continue? [y/N]: " response
    case "$response" in
      [yY][eE][sS]|[yY])
        generate_report
        ;;
      *)
        exit 0
        ;;
    esac
    ;;
  *)
    usage
    ;;
esac
