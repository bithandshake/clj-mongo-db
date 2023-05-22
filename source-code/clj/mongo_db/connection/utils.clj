
(ns mongo-db.connection.utils
    (:require [mongo-db.core.errors      :as core.errors]
              [mongo-db.connection.state :as connection.state]
              [noop.api                  :refer [return]]
              [string.api                :as string]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn default-database-name
  ; @ignore
  ;
  ; @return (string)
  []
  (let [connection-count (-> @connection.state/REFERENCES keys count)]
       (case connection-count 0 (throw (Exception. core.errors/MISSING-DATABASE-NAME-AND-NO-CONNECTION-ERROR))
                              1 (-> @connection.state/REFERENCES keys first)
                                (throw (Exception. core.errors/MISSING-DATABASE-NAME-AND-MULTI-CONNECTION-ERROR)))))

(defn collection-path->database-name
  ; @ignore
  ;
  ; @description
  ; Derives the database name from the 'collection-path' before the "/" character.
  ; If no database name found in the 'collection-path' and only one database connection
  ; estabilished, returns the only connected database name.
  ;
  ; @usage
  ; (collection-path->database-name "my_collection")
  ;
  ; @usage
  ; (collection-path->database-name "my-database/my_collection")
  ;
  ; @example
  ; (collection-path->database-name "my_collection")
  ; =>
  ; "the-only-connected-database-name"
  ;
  ; @example
  ; (collection-path->database-name "my-database/my_collection")
  ; =>
  ; "my-database"
  ;
  ; @return (string)
  [collection-path]
  (if-let [database-name (string/before-first-occurence collection-path "/" {:return? false})]
          (return database-name)
          (default-database-name)))

(defn collection-path->collection-name
  ; @ignore
  ;
  ; @usage
  ; (collection-path->collection-name "my_collection")
  ;
  ; @usage
  ; (collection-path->collection-name "my-database/my_collection")
  ;
  ; @example
  ; (collection-path->collection-name "my_collection")
  ; =>
  ; "my_collection"
  ;
  ; @example
  ; (collection-path->collection-name "my-database/my_collection")
  ; =>
  ; "my_collection"
  ;
  ; @return (string)
  [collection-path]
  (string/after-first-occurence collection-path "/" {:return? true}))