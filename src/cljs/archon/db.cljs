(ns archon.db)

(def default-db
  {:name "glow"
   :active-panel :city-input-panel   ;; the active view to show
   :city-name "city"                 ;; the city we want to limit our vendor search to
   :current-vendor-info {}           ;; information for the vendor currently being examined
   :vendor-reg {                     ;; a map of all the information collected on the vendor reg panel
     :vr-email "email address"         ;; the email address we will send a registration invite email to
     :vr-pwd nil                       ;; the password for vendor registration
     :vr-conf-pwd nil                  ;; the confirmed password for vendor reg
     :vr-first-name nil                ;; vendor first name
     :vr-last-name nil                 ;; vendor last name
     :vr-addr-str-num nil              ;; vendor street number
     :vr-addr-str-name nil             ;; vendor street name
     :vr-addr-city nil                 ;; vendor city
     :vr-addr-state nil                ;; vendor state
     :vr-addr-postal nil               ;; vendor postal code
     :vr-phone nil}                    ;; vendor phone number
   :prev-state {}})                ;; holds the spot we want to jump back to after registration email sent
                                   ;; holds the spot we want to jump back to after registration email sent
   :user {
          :profile {}                  ;; holds the auth0 user profile data structure
          :auth-result {}}})           ;; holds the auth0 authentication result

