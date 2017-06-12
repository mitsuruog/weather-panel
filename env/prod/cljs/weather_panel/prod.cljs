(ns weather-panel.prod
  (:require [weather-panel.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
