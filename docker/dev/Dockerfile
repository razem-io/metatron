FROM openjdk:8-jre

ADD target/universal/metatron-*.tgz /
RUN mv /metatron-* /metatron

CMD ["/metatron/bin/metatron"]