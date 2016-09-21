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

nginx -c "$PREFIX_PATH/conf/conf" -p "$PREFIX_PATH" -s stop
