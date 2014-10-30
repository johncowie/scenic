(ns scenic.test.routes
  (:require [midje.sweet :refer :all]
            [scenic.routes :refer [load-routes]]
            [bidi.bidi :refer [path-for match-route]]
            ))

(facts "Can load routes"
       (fact "basic route"
             (load-routes "GET / home") =>  [["/" {:get :home}]])
       (fact "two routes"
             (load-routes "GET / home\n
                           GET /hello hello") => [["/" {:get :home}]
                                                  ["/hello" {:get :hello}]])
       (fact "route with path param"
             (load-routes "GET /hello/:name hello") => [[["/hello/" :name] {:get :hello}]])
       (future-fact "support context..."
             (load-routes "/home \n GET / home") => [["/home/" {:get :home}]]
             (load-routes "/a \n GET /b b \n GET /c c") => [["/a/b" {:get :b}] ["/a/c" {:get :c}]]
             )
       )

(future-fact "knit-contexts"
       ;(knit-contexts [["/home"] ["GET" "/a" "a"]]) => [["GET" "/home/a" "a"]]
       )

(future-fact "can load from file")

(future-fact "can make handler from route file and handler map")

(future-fact "path for..")

;(path-for ["" [[["/"] {:get  :home}] [["/hello/" :name] {:get :hello}]]] :home)
