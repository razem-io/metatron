FROM openjdk:8-jre

ADD modules/fetchers/homematic/target/universal/metatron_fetcher_homematic-*.tgz /
RUN mv /metatron_fetcher_homematic-* /app

CMD ["/app/bin/metatron_fetcher_homematic"]