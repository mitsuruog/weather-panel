(ns weather-panel.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs-http.client :as http]
              [cljs.core.async :refer [<!]]))

;; -------------------------
;; Global stete
(defonce app-state (atom {:count 0}))


;; -------------------------
;; Functions
(defn fetchWeatherData []
  (js/console.log "Hello"))

;; https://query.yahooapis.com/v1/public/yql?q=
;; select * from weather.forecast where woeid in (select woeid from geo.places(1) where text="tokyo")
;; https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22tokyo%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=
;; select * from weather.forecast where woeid in (SELECT woeid FROM geo.placefinder WHERE text="35,135" and gflags="R")


;; -------------------------
;; Stateless Components

(defn temperature []
  [:div.temperature
    [:span "25"]
    [:span "15"]
  ])

(defn day []
  [:div.day
    [:div.day-of-the-week "SUN"]
    [:div.weather-icon
      [:i.wi.wi-day-sunny]
    ]
    [temperature]
    [:div.rainy-percent "15"]
  ])

(defn today[]
  [:div.today
    [:div.weather-icon
      [:i.wi.wi-day-sunny]
    ]
    [:div.weather-detail
      [:div.current-temperature "20"]
      [:div.name "sunny"]
      [temperature]
      [:div:rainy-percent "0"]
    ]
  ])

;; -------------------------
;; App

(defn app []
  [:div.weather
    [today]
    [day]
    ; (for [day [1,2,3,4,5]]
    ;   ^{:key day} [day])
    [:button {:on-click #(swap! app-state assoc :count (.getTime (js/Date.)))} "click"]
    [:div (:count @app-state)]
  ])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [app] (.getElementById js/document "app"))
  (fetchWeatherData))

(defn init! []
  (mount-root))
