#!/bin/bash
set -x
#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT=" -server -Xms${JVM_XMS} -Xmx${JVM_XMX} -Xmn${JVM_XMN} -XX:MetaspaceSize=${JVM_MS} -XX:MaxMetaspaceSize=${JVM_MMS}"

#===========================================================================================
# Setting service properties
#===========================================================================================

PORT=0
#判断当前端口是否被占用，没被占用返回0，反之1
function Listening {
   TCPListeningnum=`netstat -an | grep ":$1 " | awk '$1 == "tcp" && $NF == "LISTEN" {print $0}' | wc -l`
   UDPListeningnum=`netstat -an | grep ":$1 " | awk '$1 == "udp" && $NF == "0.0.0.0:*" {print $0}' | wc -l`
   (( Listeningnum = TCPListeningnum + UDPListeningnum ))
   if [ $Listeningnum == 0 ]; then
       echo "0"
   else
       echo "1"
   fi
}

#指定区间随机数
function random_range {
   shuf -i $1-$2 -n1
}

#得到随机端口
function get_random_port {
   templ=0
   while [ $PORT == 0 ]; do
       temp1=`random_range $1 $2`
       if [ `Listening $temp1` == 0 ] ; then
              PORT=$temp1
       fi
   done
   echo "port=$PORT"
   APP_OPT="${APP_OPT} --server.port=${PORT}"
}
get_random_port 10010 10019; #这里指定了1~10000区间，从中任取一个未占用端口号


#nacos
if [[ ! -z "${NACOS_SERVER_IP}" ]]; then
    APP_OPT="${APP_OPT} --ind.nacos.url=${NACOS_SERVER_IP}"
    if [[ ! -z "${NACOS_SERVER_PORT}" ]]; then
        APP_OPT="${APP_OPT}:${NACOS_SERVER_PORT}"
    else
        APP_OPT="${APP_OPT}:8848"
    fi
fi

if [[ ! -z "${NACOS_USERNAME}" ]]; then
    APP_OPT="${APP_OPT} --ind.nacos.username=${NACOS_USERNAME}"
fi

if [[ ! -z "${NACOS_PASSWORD}" ]]; then
    APP_OPT="${APP_OPT} --ind.nacos.password=${NACOS_PASSWORD}"
fi

#redis
if [[ ! -z "${REDIS_SERVER_IP}" ]]; then
    APP_OPT="${APP_OPT} --ind.redis.host=${REDIS_SERVER_IP}"
fi

if [[ ! -z "${REDIS_SERVER_PASSWORD}" ]]; then
    APP_OPT="${APP_OPT} --ind.redis.password=${REDIS_SERVER_PASSWORD}"
fi

#datasource
if [[ ! -z "${DATASOURCE_IP}" ]]; then
    APP_OPT="${APP_OPT} --ind.datasource.url=${DATASOURCE_IP}"
fi
if [[ ! -z "${DATASOURCE_USERNAME}" ]]; then
    APP_OPT="${APP_OPT} --ind.datasource.username=${DATASOURCE_USERNAME}"
fi

if [[ ! -z "${DATASOURCE_PASSWORD}" ]]; then
    APP_OPT="${APP_OPT} --ind.datasource.password=${DATASOURCE_PASSWORD}"
fi

if [[ ! -z "${SKYWALKING_SERVICE}" ]]; then

  JAVA_OPT="${JAVA_OPT} -javaagent:${BASE_DIR}/agent/skywalking-agent.jar"

  if [[ ! -z "${APP_NAME}" ]]; then
    JAVA_OPT="${JAVA_OPT} -Dskywalking.agent.service_name=${APP_NAME}"
  else
    JAVA_OPT="${JAVA_OPT} -Dskywalking.agent.service_name=ind-auth"
  fi

  JAVA_OPT="${JAVA_OPT} -Dskywalking.collector.backend_service=${SKYWALKING_SERVICE}"

fi

JAVA_OPT="${JAVA_OPT} -Dfile.encoding=utf-8 -jar ${BASE_DIR}/app.jar"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
JAVA_OPT="${JAVA_OPT} ${APP_OPT} "

echo "service is starting,you can check the ${BASE_DIR}/logs/${APP_NAME}/start.out"
echo "java ${JAVA_OPT}" > ${BASE_DIR}/logs/${APP_NAME}/start.out 2>&1 &
nohup java ${JAVA_OPT} > ${BASE_DIR}/logs/${APP_NAME}/start.out 2>&1 < /dev/null
