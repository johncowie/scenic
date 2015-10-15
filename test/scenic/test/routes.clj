(ns scenic.test.routes
  (:require [midje.sweet :refer :all]
            [scenic.routes :refer [parse-path-segment load-routes scenic-handler load-routes-from-file regex]]
            [bidi.bidi :refer [path-for match-route]]
            [ring.mock.request :refer [request]]))

(facts "Processing path params"
       (parse-path-segment "bob") => "bob"
       (parse-path-segment ":dave") => :dave
       (parse-path-segment ":id~>[a-b]*") => [(regex "[a-b]*") :id])

(facts "Can load routes"
       (fact "basic route"
             (load-routes "GET / home") =>  ["" [["/" {:get :home}]]])
       (fact "two routes"
             (load-routes "GET / home\n
                           GET /hello hello") => ["" [["/" {:get :home}]
                                                      ["/hello" {:get :hello}]]])
       (fact "route with path param"
             (load-routes "GET /hello/:name hello") => ["" [[["/hello/" :name] {:get :hello}]]])
       (fact "can tolerate blank lines"
             (load-routes "GET / home \n      \n    \n GET /hello hello")
             => ["" [["/" {:get :home}]
                     ["/hello" {:get :hello}]]])
       (fact "can specify a regex"
             (load-routes "GET /:name~>[a-z]*/hello hello")
                    => ["" [[["/" [(regex "[a-z]*") :name] "/hello"] {:get :hello}]]])
       (future-fact "support contexts "))

(facts "Can load routes from file"
       (fact "loads routes from a file"
             (load-routes-from-file "routes.txt") => ["" '(["/" {:get :home}])])
       (fact "throws exception when the file doesn't exist"
             (load-routes-from-file "xxx") => (throws Exception "Routes file not found")))

(facts "about scenic-handler"
 (fact "can make handler from routes file and handler map"
       (let [routes (load-routes "GET /   home \n
                                  GET /p1 page1")
             handlers {:home (constantly "HOME")
                       :page1 (constantly "PAGE1")}
             app (scenic-handler routes handlers)]
         (-> (request :get "/") app) => "HOME"
         (-> (request :get "/p1") app) => "PAGE1"
         (-> (request :get "/blah") app) => nil))
 (fact "can supply optional not-found handler"
       (let [routes (load-routes "GET / home")
             handlers {:home (constantly "HOME")}
             app (scenic-handler routes handlers (constantly "404"))]
         (-> (request :get "/") app) => "HOME"
         (-> (request :get "/blah") app) => "404")))
