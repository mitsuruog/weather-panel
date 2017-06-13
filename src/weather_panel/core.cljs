(ns weather-panel.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [reagent.core :as reagent]
              [cljs-http.client :as http]
              [cljs.core.async :refer [<!]]))

;; -------------------------
;; Global state
(defonce app-state (reagent/atom {
    :count 0
    :weather {}
  }))


;; -------------------------
;; Functions
(defn fetchWeatherData [position]
  (js/console.log "fetchWeatherData" position)
  (go (let [response (<! (http/get "http://api.openweathermap.org/data/2.5/weather"
    {:with-credentials? false
     :query-params {
          "APPID" "3a5d16a9915a78f288b845232e1911b4"
          "lat" position.coords.latitude
          "lon" position.coords.longitude
          "units" "metric"
          "lang" "ja"
        }}))]
    (prn (:status response))
    (prn "Weather is " (:body response)))))


(defn fetchWeeklyForecast [position]
  (js/console.log "fetchWeeklyForecast" position)
  (go (let [response (<! (http/get "http://api.openweathermap.org/data/2.5/forecast/daily"
    {:with-credentials? false
      :query-params {
           "APPID" "3a5d16a9915a78f288b845232e1911b4"
           "lat" position.coords.latitude
           "lon" position.coords.longitude
           "cnt" 5
           "units" "metric"
           "lang" "ja"
         }}))]
     (prn (:status response))
     (prn "Weekly forecast is " (:body response)))))


(defn getGeoCoordinats []
  (js/console.log "getGeoCoordinats")
  (js/navigator.geolocation.getCurrentPosition (fn [position]
    (fetchWeatherData position)
    (fetchWeeklyForecast position))))

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
  (reagent/create-class {
    :component-will-mount (fn [] (getGeoCoordinats))
    :reagent-render (fn []
      [:div.weather
        [today]
        [day]
        ; (for [day [1,2,3,4,5]]
        ;   ^{:key day} [day])
        [:button {:on-click #(swap! app-state assoc :count (.getTime (js/Date.)))} "click"]
        [:div (:count @app-state)]
      ])
    }))

; (defn some-fn []
;   (let [weather (getGeoCoordinats)]
;     (fn []
;       [weather-panel (:weather @app-state)])))
;
; (defn new-app []
;   (let [_ (getGeoCoordinats)]
;     (fn []
;       [:div.weather
;         [today]
;         [day]
;         [:button {:on-click #(swap! app-state assoc :count (.getTime (js/Date.)))} "click"]
;         [:div (:count @app-state)]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
