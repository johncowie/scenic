(ns scenic.routes
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [bidi.ring :as ring]))

(def regex (memoize re-pattern))

(defn first-not-nil-> [v & fns]
  (first (drop-while not (map #(% v) fns))))

(defn break-by-line [s]
  (string/split s #"\s*\n\s*"))

(defn break-by-space [line]
  (string/split line #"\s+"))

(def lowercase-keyword
  (comp keyword string/lower-case))

(defn parse-param [s]
  (when-let [[_ capture] (re-matches #":(.+)" s)]
    (keyword capture)))

(defn parse-param-with-regex [s]
  (when-let [[_ param-capture regex-capture] (re-matches #":(.+)~>(.+)" s)]
    [(regex regex-capture) (keyword param-capture)]))

(defn parse-path-segment [s]
  (first-not-nil->
    s
    parse-param-with-regex
    parse-param
    identity))

(defn join-string-group [[f & r]]
  (if (string? f)
    (apply str f r)
    f))

(defn if-one-return-single [[f & r :as v]]
  (if (nil? r) f v))

(defn join-adjacent-strings [v]
  (->> v
       (partition-by string?)
       (map join-string-group)
       vec))

(defn process-path [path]
  (->> (re-seq #"/|[^/]+" path)
       (map parse-path-segment)
       join-adjacent-strings
       if-one-return-single))

(defn process-route-vector [[method-str path-str action-str]]
  (let [method (lowercase-keyword method-str)
        action (keyword action-str)
        path (process-path path-str)]
    [path {method action}]))

(defn load-routes [string]
  ["" (->> string
           break-by-line
           (map break-by-space)
           (map process-route-vector))])

(defn load-routes-from-file [file]
  (if-let [resource (io/resource file)]
    (load-routes (slurp resource))
    (throw (ex-info "Routes file not found" {:path file}))))

; TODO validate request method

(defn look-up-handler [handler-map]
  (fn [id]
    (or
      (get handler-map id)
      (constantly nil))))

(defn wrap-not-found [handler not-found-handler]
  (fn [request]
    (or (handler request)
        (not-found-handler request))))

(defn scenic-handler
  ([routes handler-map]
   (ring/make-handler
     routes
     (look-up-handler handler-map)))
  ([routes handler-map not-found-handler]
   (->
     (scenic-handler routes handler-map)
     (wrap-not-found not-found-handler))))
