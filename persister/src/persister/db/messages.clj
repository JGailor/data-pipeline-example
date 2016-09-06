(ns persister.db.messages
  (:require [hugsql.core :as hugsql]
            [clojure.java.jdbc :refer [get-connection]]
            [jdbc.pool.c3p0    :as pool]
            [clj-time.coerce :as c]))

(defn to-sql-date [date]
  (if date
    (c/to-sql-date date)
    nil))

(defn prepare-message [message]
  (let [params (select-keys message [:id :type :title :description :runtime :airdate])]
    (if (contains? params :airdate)
      (update params :airdate to-sql-date)
      params)))

(def db (pool/make-datasource-spec
          {:subprotocol "postgresql"
           :subname "//localhost:5432/data_pipeline"}))

(hugsql/def-db-fns "sql/messages.sql")

(defn migrate []
  (create-messages-table db))

(defn store-message
  [message]
  (let [params (prepare-message message)]
    (try
      (save-message db params)
      (catch Exception e (println (.getNextException e))))))