#!/bin/bash

while [[ $# -gt 1 ]]
do
    key="$1"

    case $key in
        -p)
            PREFIX_PATH="$2"
            shift # past argument=value
            ;;
        *)
            # unknown option
            ;;
    esac
    shift
done

if [ -z "$PREFIX_PATH" ]; then PREFIX_PATH="$(pwd)/../api"; fi

./build.sh

nginx -c "$PREFIX_PATH/nginx.conf" -p "$PREFIX_PATH"

./sbt.sh
