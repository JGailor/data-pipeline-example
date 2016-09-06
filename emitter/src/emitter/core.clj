(ns emitter.core
  (:require [langohr.core      :as rmq]
            [langohr.channel   :as rch]
            [langohr.queue     :as rq]
            [langohr.consumers :as rc]
            [langohr.basic     :as rb]
            [clojure.data.json :as json])
  (:gen-class))

(defn -main
  [& args]
  (let [conn (rmq/connect {:host "192.168.33.33" :username "developer" :password "repoleved"})
        channel (rch/open conn)
        qname "pipeline.data.video"]
    (rq/declare channel qname {:exclusive false :auto-delete true})
    (println "[Starting emission]")
    (loop [id 1]
      (if (<= id 1000000)
        (let [msg {:type "program" :id (format "TMSID%012d" id) :title "Johnny Come Lately" :description "Double down old johnny.  Double down.  You silly jackalope." :runtime 150 :airdate "1950-05-20"}]
          (rb/publish channel "" qname (json/write-str msg) {:content-type "text/plain" :type "greetings.hi"})
          (recur (inc id)))))
    (println "[Going to sleep...]")
    (Thread/sleep 80000)
    (rmq/close channel)
    (rmq/close conn)))
