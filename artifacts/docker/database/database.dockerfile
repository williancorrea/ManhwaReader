FROM mariadb:11.8.2

RUN apt-get update
RUN apt-get install -y mariadb-client

RUN mkdir -p /app

COPY docker/database/database.sh /app/
COPY docker/database/entrypoint-wrapper.sh /app/

RUN chmod +x /app/database.sh /app/entrypoint-wrapper.sh
RUN sed -i -e 's/\r$//' /app/database.sh

ENTRYPOINT ["/app/entrypoint-wrapper.sh"]
CMD ["mariadbd", "--lower_case_table_names=1"]
