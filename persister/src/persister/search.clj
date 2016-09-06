(ns persister.search
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(def messages-index "messages")

(def messages-doc "message")

(def messages-mapping {messages-doc
                        {:properties
                          {:id {:type "string" :store "yes"}
                           :title {:type "string" :store "yes" :analyzer "snowball"}
                           :description {:type "string" :analyzer "snowball"}
                           :runtime {:type "integer"}
                           :airdate {:type "date"}
                          }}})

(def conn (esr/connect "http://192.168.33.43:9200"))

(defn init
  (if (not (esi/exists? conn messages-index))
    (esi/create conn messages-index :mappings messages-mapping)))

(defn store-message
  [message]
  (esd/put conn messages-index messages-doc (:id message) message))