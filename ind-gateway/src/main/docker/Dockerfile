# 基于centos系统
#FROM centos:7.5.1804
FROM 192.168.4.53/ind-cloud-new/openjdk:jdk-alpine
MAINTAINER wenfl "461631549@qq.com"

# set environment
ENV BASE_DIR="/home/ind-cloud-new" \
    TIME_ZONE="Asia/Shanghai" \
    LOGS_DIR="/home/ind-cloud-new/logs" \
    NACOS_SERVER_PORT="8848" \
    JVM_XMS="128m" \
    JVM_XMX="128m" \
    JVM_XMN="215m" \
    JVM_MS="128m" \
    JVM_MMS="320m"

WORKDIR /$BASE_DIR

ADD ind-gateway-1.0.RELEASE.jar app.jar
ADD docker-startup.sh /docker-startup.sh

# 对文件进行转换
RUN apk add dos2unix
#解决导出excel报错问题-https://blog.csdn.net/qq_31263039/article/details/106545833
RUN apk add --update ttf-dejavu fontconfig
RUN dos2unix /docker-startup.sh

# set startup log dir
RUN mkdir -p $LOGS_DIR \
	&& cd $LOGS_DIR \
	&& touch start.out \
	&& ln -sf /dev/stdout start.out \
    && ln -sf /dev/stderr start.out
RUN chmod +x /docker-startup.sh

ENTRYPOINT ["/docker-startup.sh"]
