(ns scenic.test.routes
  (:require [midje.sweet :refer :all]
            [scenic.routes :refer [load-routes scenic-handler]]
            [bidi.bidi :refer [path-for match-route]]
            [ring.mock.request :refer [request]]
            ))

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
       (future-fact "support contexts "))

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
