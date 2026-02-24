#!/bin/bash

function must_create_db() {
  local DATABASE_NAME=$1
  # shellcheck disable=SC2006
#  RESULT=$(mysqlshow --host="${CONTAINER_DB}" --user=root --password="${MYSQL_ROOT_PASSWORD}" "${DATABASE_NAME}" 2>/dev/null | grep -v Wildcard | grep -o "${DATABASE_NAME}")
  RESULT=$(mariadb-show --host="${CONTAINER_DB}" --user=root --password="${MYSQL_ROOT_PASSWORD}" "${DATABASE_NAME}" 2>/dev/null | grep -v Wildcard | grep -o "${DATABASE_NAME}")
#  RESULT=$(mariadb-show --host="${CONTAINER_DB}" --user=root --password="${MYSQL_ROOT_PASSWORD}" "${DATABASE_NAME}" | grep -v Wildcard | grep -o "${DATABASE_NAME}")
  if [ "$RESULT" = "${DATABASE_NAME}" ]; then
    echo false
  else
    echo true
  fi
}

function create_db() {
  local DB_NAME=$1
  local DB_USER=$2
  local DB_PASS=$3
    
  echo " * Checking the need to create a database (${DB_NAME})"
  VALIDATE=$(must_create_db "${DB_NAME}")

  if [ "$VALIDATE" = true ] ; then
    echo "   - Creating database (${DB_NAME}) for identity manager server..."
    mariadb --host="${CONTAINER_DB}" --user=root --password="${MYSQL_ROOT_PASSWORD}" <<EOF
CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET = 'utf8' COLLATE = 'utf8_general_ci';
CREATE USER IF NOT EXISTS '${DB_USER}'@'%' IDENTIFIED BY '${DB_PASS}';
GRANT ALL ON ${DB_NAME}.* TO '${DB_USER}'@'%';
FLUSH PRIVILEGES;
EOF
  fi
}

function wait_for_database_server() {
  echo " * Starting check database status"
  until mariadb \
    --host=${CONTAINER_DB} \
    --user=root \
    --password=${MYSQL_ROOT_PASSWORD} \
    --batch \
    --execute="SELECT NOW();" mysql 2>/dev/null 1>/dev/null; do
    sleep 2
    echo " * Waiting database server to come online..."
  done
  echo
}

function main(){
  echo "Removing completion validation file (initialization_completed)"
  rm -f  /app/.initialization_completed 2>/dev/null 1>/dev/null
  
  echo " * Starting"
  wait_for_database_server

  #Create databases from list
  while IFS=':' read -r DB_NAME DB_USER DB_PASS; do
    [ -z "$DB_NAME" ] && continue  # Ignore empty lines
    create_db "$DB_NAME" "$DB_USER" "$DB_PASS"
  done <<< "$DB_SCHEMAS"
  
  echo ""
  echo " * Creating completion validation file (initialization_completed)"
  touch /app/.initialization_completed
  
  echo ""
  echo " * Successfully Configured"
  echo ""
  
}

main
