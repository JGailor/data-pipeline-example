-- resources/sql/messages.sql
-- Working with messages

-- :name    create-messages-table
-- :command :execute
-- :result  :raw
-- :doc     Creates the messages table
create table if not exists messages (
  id          varchar(24)  PRIMARY KEY,
  type        varchar(32)  NOT NULL,
  title       varchar(255) NOT NULL,
  description varchar(255) NOT NULL,
  runtime     integer,
  airdate     date
)

-- :name save-message :! :n
-- :doc  Saves a single message to the database
insert into messages (id, type, title, description, runtime, airdate)
  values (:id, :type, :title, :description, :runtime, :airdate)