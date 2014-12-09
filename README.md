# scenic

Scenic is a simple thin wrapper around http://github.com/juxt/bidi that translates routes in a routes.rb-ish file to a Bidi routes data structure.  

## Example Usage

Create a routes file and put it somewhere on the class path (eg. my_project/resources/my_routes.routes). It should have the format ```<METHOD> <PATH> <ID>```

The `<ID>` is a keyword which should be used to point to a controller function.

```
GET   /       home
GET   /login  login-form
POST  /login  login
```

Then...

```clojure
(ns my_project.core
  (:require [scenic.routes] :refer :all))

(defn home [request]
  "hello")

(def handler 
  (scenic-handler (load-routes-from-file "my_routes.routes") {:home home})

(defn -main [& args]
  run-server handler {:port 9001})
```

## License

Copyright © 2014 John Cowie

Distributed under the Eclipse Public License, the same as Clojure.
