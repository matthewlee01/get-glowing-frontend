(ns archon.db)

(def default-db
  {:name "glow"
   :active-panel :city-input-panel   ;; the active view to show
   :city-name "city"                 ;; the city we want to limit our vendor search to
   :current-vendor-info {}           ;; information for the vendor currently being examined
   :vr-email "email address"         ;; the email address we will send a registration invite email to
   :vr-pwd nil                       ;; the password for vendor registration
   :vr-conf-pwd nil                  ;; the confirmed password for vendor reg
   :vr-first-name nil                ;; vendor first name
   :vr-last-name nil                 ;; vendor last name
   :vr-phone nil                     ;; vendor phone number
   :prev-state {}})                  ;; holds the spot we want to jump back to after registration email sent
