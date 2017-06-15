(ns weather-panel.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

;; -------------------------
;; Global state
(defonce app-state (reagent/atom {:currentWeather {}
                                  :weeklyForecast {}}))

;; -------------------------
;; Functions
(defn fetchWeatherData [position]
  (js/console.log "fetchWeatherData" position)
  (go (let [response (<! (http/get "http://api.openweathermap.org/data/2.5/weather"
                                   {:with-credentials? false
                                    :query-params {"APPID" "3a5d16a9915a78f288b845232e1911b4"
                                                   "lat" position.coords.latitude
                                                   "lon" position.coords.longitude
                                                   "units" "metric"
                                                   "lang" "ja"}}))]
        (swap! app-state assoc :currentWeather (:body response))
        (prn "Current weather is " (:currentWeather @app-state)))))

(defn fetchWeeklyForecast [position]
  (js/console.log "fetchWeeklyForecast" position)
  (go (let [response (<! (http/get "http://api.openweathermap.org/data/2.5/forecast/daily"
                                   {:with-credentials? false
                                    :query-params {"APPID" "3a5d16a9915a78f288b845232e1911b4"
                                                   "lat" position.coords.latitude
                                                   "lon" position.coords.longitude
                                                   "cnt" 5
                                                   "units" "metric"
                                                   "lang" "ja"}}))]
        (swap! app-state assoc :weeklyForecast (:body response))
        (prn "Weekly forecast is " (:weeklyForecast @app-state)))))

(defn getGeoCoordinats []
  (js/console.log "getGeoCoordinats")
  (js/navigator.geolocation.getCurrentPosition (fn [position]
                                                 (prn "Position is " position)
                                                 (fetchWeatherData position)
                                                 (fetchWeeklyForecast position))))

(defn getDay [timestamp]
  (let [days ["SUN","MON","TUE","WED","THU","FRI","SAT"]]
    (days (.getDay (js/Date. timestamp)))))

;; -------------------------
;; Stateless Components

(defn weatherIcon [id]
  [:i.wi {:class (str "wi-owm-" id)}])

(defn temperature [max min]
  [:div.temperature
   [:span (js/Math.round max)]
   [:span " / "]
   [:span (js/Math.round min)]])

(defn day [forecast]
  (let [temp (:temp forecast)
        weather (first (:weather forecast))]
    [:div.day
     [:div.day-of-the-week (getDay (* 1000 (:dt forecast)))]
     [:div.weather-icon
      [weatherIcon (:id weather)]]
     [temperature (:max temp) (:min temp)]
     [:div.rainy-percent "15"]]))

(defn week []
  (let [weeklyForecast (:weeklyForecast @app-state)
        list (:list weeklyForecast)]
    [:div.week
     (for [forecast list]
       ^{:key forecast} (day forecast))]))

(defn today []
  (let [currentWeather (:currentWeather @app-state)
        main (:main currentWeather)
        weather (first (:weather currentWeather))]
    [:div.today
     [:div.weather-icon
      [weatherIcon (:id weather)]]
     [:div.weather-detail
      [:div.current-temperature (js/Math.round (:temp main))]
      [:div.name (:description weather)]
      [temperature (:temp_max main) (:temp_min main)]
      [:div:rainy-percent "0"]]]))

(defn app []
  (let [_ (getGeoCoordinats)]
    (fn []
      [:div.weather
       [today]
       [week]])))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [app] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
