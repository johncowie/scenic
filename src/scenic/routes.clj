(ns scenic.routes
  (:require [bidi.bidi :as bidi]))

(defn break-by-line [s]
  (clojure.string/split s #"\s*\n\s*"))

(defn break-by-space [line]
  (clojure.string/split line #"\s+"))

(def lowercase-keyword
  (comp keyword clojure.string/lower-case))

(defn keywordise-path-param [s]
  (if (re-matches #":.+" s)
    (-> s (subs 1) keyword)
    s))

(defn join-string-group [[f & r]]
  (if (string? f)
    (apply str f r)
    f))

(defn if-one-return-single [[f & r :as v]]
  (if (nil? r) f v))

(defn process-path [path]
  (->>
   (re-seq #"/|[^/]+" path)
   (map keywordise-path-param)
   (partition-by string?)
   (map join-string-group)
   vec
   if-one-return-single))

(defn process-route-vector [[method-str path-str action-str]]
  (let [method (lowercase-keyword method-str)
        action (keyword action-str)
        path (process-path path-str)]
    [path {method action}]))

(defn load-routes [string]
  (->> string
      break-by-line
      (map break-by-space)
      (map process-route-vector)
      ))

(defn load-routes-from-file [file]
  ["" (load-routes (slurp (clojure.java.io/resource file)))])

; TODO validate request method

(defn path [action & params]
  (apply bidi/path-for routes action params))

(defn look-up-handler [handler-map]
  (fn [id]
    (or
      (id handler-map)
      (constantly nil))))

(defn scenic-handler [routes-file handler-map]
  (bidi/make-handler
   (load-routes-from-file routes-files)
   (look-up-handler handler-map)))
