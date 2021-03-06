(ns examples.server.step-3
  (:use [compojure.core :only (defroutes GET POST)]
        [examples.snippet]
        [ring.util.response :only (redirect)])
  (:require [hiccup.core :as hiccup]
            [hiccup.form-helpers :as form]
            [hiccup.page-helpers :as page]
            [compojure.route :as route]
            [compojure.handler :as handler]))

; START: layout
(defn layout [title & body]
  (hiccup/html
   [:head
    [:title title]
    (page/include-js "/public/javascripts/code-highlighter.js"
                     "/public/javascripts/clojure.js")
    (page/include-css "/public/stylesheets/code-highlighter.css")]
   [:body
    [:h2 title]
    body]))
; END: layout

; START: new-snippet
(defn new-snippet []
  (layout "Create a Snippet"
          (form/form-to [:post "/"]
                        (form/text-area {:rows 20 :cols 73} "body")
                        [:br]
                        (form/submit-button "Save"))))
; END: new-snippet

(defn create-snippet [body]
  (if-let [id (insert-snippet body)]
    (redirect (str "/" id))
    (redirect "/")))

; START: show-snippet
(defn show-snippet [id]
  (layout (str "Snippet " id)
          (let [snippet (select-snippet id)]
            (hiccup/html
             [:div [:pre [:code.clojure (:body snippet)]]]
             [:div (:created_at snippet)]))))
; END: show-snippet

; START: public
(defroutes routes
  (GET "/" [] (new-snippet))
  (GET "/:id" [id] (show-snippet id))
  (POST "/" [body] (create-snippet body))
  (route/files "/")
  (route/not-found "<h2>Not Found</h2>"))
; END: public

(def application
  (handler/site routes))