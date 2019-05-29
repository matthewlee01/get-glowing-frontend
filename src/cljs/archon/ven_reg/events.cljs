(ns archon.ven-reg.events
  (:require
    [re-frame.core :as rf]
    [archon.db :as db]
    [archon.config :as config]
    [archon.routes :as routes]
    [clojure.spec.alpha :as s]
    [clojure.string :as str]
    [ajax.core :as ajax :refer [json-request-format 
                                json-response-format]]))

(rf/reg-event-fx
  ::submit-vendor-registration
  (fn [_world [_ vendor_id]]
    {:http-xhrio {:method  :post
                  :uri    config/ven-reg-url
                  :params {:query (str "query vendor_by_id($id:Int!)"
                                       "{vendor_by_id (vendor_id: $id)"
                                       "{vendor_id name_first profile_pic"
                                       " services{s_description s_duration s_name s_price s_type}}}")
                           :variables {:id vendor_id}}
                  :timeout 3000
                  :format (json-request-format)
                  :response-format (json-response-format {:keywords? true})
                  :on-success [::good-result]
                  :on-failure [::bad-result]}}))

(rf/reg-event-db
  ::good-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (let [ven-details (:vendor_by_id data)
          ven-id (:vendor_id ven-details)
          match (routes/url-for ::routes/vendor-details-panel {:vendor-id ven-id})
          url-string (:path match)]

      ;; set the new URL so that the view is updated
      (routes/set-history url-string)
      (assoc db :vendor-details ven-details))))

(rf/reg-event-db
  ::bad-result
  (fn [db [_ {:keys [data errors] :as payload}]]
    (config/debug-out (str "BAD data: " payload))
    (assoc db :active-panel :services-panel)))

(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$") ; some characters + @ + some characters + 2-63 letters

(def num-regex #"^[0-9]+")

(s/def ::email-type (s/and string? #(re-matches email-regex %)))

(s/def ::num-type (s/and string? #(re-matches num-regex %)))

(defn valid-email
  [email]
  (let [checked-email (s/conform ::email-type email)]
    (if (= checked-email ::s/invalid)
      (do (js/alert "oops! bad email")
        false)
      (do (js/alert "good email!")
        checked-email))))

(defn valid-num
  [num]
  (let [checked-num (s/conform ::num-type num)]
    (if (= checked-num ::s/invalid)
      (do (js/alert "oops! bad address number")
        false)
      (do (js/alert "good address number!")
        checked-num))))

(rf/reg-event-db
  ::show-vendor-signup-form
  (fn [db _]
    ;; this is where we push the new URL onto the browser history 
    ;; to start the navigation to the registration form
    (let [match (routes/url-for ::routes/vendor-signup-panel)
          url-string (:path match)]
      (println url-string)
      (routes/set-history url-string)

      ;; this is also where pre-population of the signup fields should be
      ;; assigned from the auth0 token

      ;; save the page we are leaving so that we can return to the
      ;; same place after the registration is complete
      (assoc db :prev-state db))))

(rf/reg-event-db
  ::vr-email-change
  (fn [db [_ vendor-email]]
    (assoc-in db [:vendor-reg :vr-email] vendor-email)))

(rf/reg-event-db
  ::vr-first-name-change
  (fn [db [_ vendor-first-name]]
    (assoc-in db [:vendor-reg :vr-first-name] vendor-first-name)))

(rf/reg-event-db
  ::vr-last-name-change
  (fn [db [_ vendor-last-name]]
    (assoc-in db [:vendor-reg :vr-last-name] vendor-last-name)))

(rf/reg-event-db
  ::vr-phone-change
  (fn [db [_ vendor-phone]]
    (assoc-in db [:vendor-reg :vr-phone] vendor-phone)))

(rf/reg-event-db
  ::vr-address-change
  (fn [db [_ vendor-address]]
    (let [[num name] (str/split vendor-address #" " 2)]
      (-> db
        (assoc-in [:vendor-reg :vr-addr-num] num)
        (assoc-in [:vendor-reg :vr-addr-name] name)))))

(rf/reg-event-db
  ::vr-city-change
  (fn [db [_ vendor-city]]
    (assoc-in db [:vendor-reg :vr-addr-city] vendor-city)))

(rf/reg-event-db
  ::vr-province-change
  (fn [db [_ vendor-state]]
    (assoc-in db [:vendor-reg :vr-addr-state] vendor-state)))

(rf/reg-event-db
  ::vr-postal-change
  (fn [db [_ vendor-postal]]
    (assoc-in db [:vendor-reg :vr-addr-phone] vendor-postal)))

(rf/reg-event-db
  ::submit-vendor-form
  (fn [db _]
    (if (-> db :vendor-reg :vr-addr-num (valid-num))
      (assoc db :active-panel :thanks-for-registering-panel)
      db)))
