(ns spirolaterals.core
  (:require [cljs.pprint :as pprint]
            [clojure.string :as s]
            [reagent.core :as r]
            [reagent.dom :as reagent-dom]))

(set! *warn-on-infer* true)

(def state* (r/atom {:points []
                     :steps 1
                     :angle 90}))

(defn- go!
  []
  (loop [x 0
         y 0
         theta 0
         points [[0 0]]
         min-x 0
         max-x 0
         min-y 0
         max-y 0
         steps (:steps @state*)
         i 1]
    (let [[new-x new-y] [(+ x (* (Math/cos theta) steps))
                         (+ y (* (Math/sin theta) steps))]
          [min-x max-x min-y max-y] [(min min-x new-x) (max max-x new-x)
                                     (min min-y new-y) (max max-y new-y)]
          points (conj points [new-x new-y])
          theta (+ theta (* (- 180 (:angle @state*)) Math/PI (/ 1 180.0)))
          steps (if (zero? (dec steps))
                  (:steps @state*)
                  (dec steps))]
      (if (or (> i 360)
              (and (< (Math/abs new-x) 0.1)
                   (< (Math/abs new-y) 0.1)
                   (zero? (mod i (:steps @state*)))
                   (zero? (mod (* (quot i (:steps @state*))
                                  (:steps @state*)
                                  (- 180 (:angle @state*))) 360))))
        (swap! state* assoc
               :points (if (> i 360) points (conj points [0 0]))
               :bbox [min-x max-x min-y max-y])
        (recur new-x new-y theta points min-x max-x min-y max-y steps (inc i))))))

(defn event->float-v
  [^js/Event e]
  (let [^js/HTMLInputElement elt (-> e .-target)]
    (-> elt .-value js/parseFloat float)))

(defn- page
  []
  (fn []
    [:div.page
     [:div.editor
      [:table
       [:tbody
        [:tr [:td "Steps:"]
         [:td [:input {:type :range :value (:steps @state*) :min 1 :max 20 :step 1
                       :on-change #(do (swap! state* assoc :steps (event->float-v %))
                                       (go!))}]]
         [:td (:steps @state*)]]
        [:tr [:td "Angle:"]
         [:td [:input {:type :range :value (:angle @state*) :min 1 :max 180 :step 1
                       :on-change #(do (swap! state* assoc :angle (event->float-v %))
                                       (go!))}]]
         [:td (:angle @state*)]]]]]
     (when-not (empty? (:points @state*))
       [:div.graphics
        (let [px-w 960
              [min-x max-x min-y max-y] (:bbox @state*)
              [w h] [(- max-x min-x) (- max-y min-y)]
              aspect (/ w h)
              [full-w full-h] [(- px-w 20) (- (/ px-w aspect) 20)]
              points (->> (:points @state*)
                          (map (fn [[x y]]
                                 [(+ 10 (* full-w (/ (- x min-x) w))) (+ 10 (* full-h (/ (- y min-y) h)))])))]
          [:svg#pic {:xmlns "http://www.w3.org/2000/svg"
                     :width px-w
                     :height (/ px-w aspect)}
           [:polyline {:points (s/join " " (map #(s/join "," %) points))
                       :stroke-width 2 :stroke :#222 :fill :none
                       :stroke-linecap :round :stroke-linejoin :round}]])
        ;; [:div [:pre (with-out-str (pprint/pprint @state*))]]
        ])]))

(reagent-dom/render [page] (js/document.getElementById "app"))

(go!)
