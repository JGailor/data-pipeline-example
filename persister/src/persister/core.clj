(ns persister.core
  (:require [langohr.core          :as rmq]
            [langohr.queue         :as rq]
            [langohr.channel       :as rch]
            [langohr.consumers     :as rc]
            [langohr.basic         :as rb]
            [clojure.data.json     :as json]
            [clj-time.core         :as t]
            [clj-time.format       :as tf]
            [persister.db.messages :as messages])
  (:gen-class))

(defn parse-date-from-string
  [datestr]
  (if datestr
    (tf/parse datestr)
    nil))

(defn to-message [metadata]
  (if (contains? metadata :airdate)
    (update metadata :airdate parse-date-from-string)
    metadata))

(defn message-handler
  [w ch meta ^bytes payload]
  (let [now (t/now)
        formatter (tf/formatters :date-time)
        metadata (json/read-str (String. payload "UTF-8") :key-fn keyword)
        enhanced (assoc metadata :persist-timestamp (tf/unparse formatter now))]
    (messages/store-message (to-message enhanced))
    (.write w (json/write-str enhanced))
    (.newLine w)))

(defn -main
  [& args]
  (messages/migrate)
  (with-open [w (clojure.java.io/writer "stuff.txt")]
    (let [conn (rmq/connect {:host "192.168.33.33" :username "developer" :password "repoleved"})
          channel (rch/open conn)
          qname "pipeline.data.video.timestamped"
          handler (partial message-handler w)]
      (rq/declare channel qname {:exclusive false :auto-delete true})
      (rc/subscribe channel qname handler {:auto-ack true})
      (println "Waiting for messages...")
      (Thread/sleep 80000)
      (rmq/close channel)
      (rmq/close conn))))
