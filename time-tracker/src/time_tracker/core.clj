(ns time-tracker.core
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as rch]
            [langohr.queue     :as rq]
            [langohr.basic     :as rb]
            [langohr.consumers :as rc]
            [clojure.data.json :as json]
            [clj-time.core     :as time]
            [clj-time.format   :as time-format])
  (:gen-class))

(defn format-date-time
  [dt]
  (let [format (time-format/formatters :date-time)]
    (time-format/unparse format dt)))

(defn video-message-handler
  [outch outq w ch meta ^bytes payload]
  (let [timestamp (format-date-time (time/now))
        metadata (json/read-str (String. payload "UTF-8"))
        enhanced-msg (assoc metadata :timestamp timestamp)]
    (.write w (json/write-str enhanced-msg))
    (.newLine w)
    (rb/publish outch "" outq (json/write-str enhanced-msg)  {:content-type "text/plain" :type "greetings.hi"})))

(defn -main
  [& args]
  (with-open [w (clojure.java.io/writer "dump.out")]
    (let [conn (rmq/connect  {:host "192.168.33.33" :username "developer" :password "repoleved"})
          rmqch (rch/open conn)
          qname "gracenote.data.video"
          handler (partial video-message-handler rmqch "gracenote.data.video.timestamped" w)]
      (rq/declare rmqch qname {:exclusive false :auto-delete true})
      (rc/subscribe rmqch qname handler {:auto-ack true})
      (println "Waiting for messages...")
      (Thread/sleep 80000)
      (rmq/close rmqch)
      (rmq/close conn))))
