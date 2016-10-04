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

nginx -c "$PREFIX_PATH/conf" -p "$PREFIX_PATH" -s stop
