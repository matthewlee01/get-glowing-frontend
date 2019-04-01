(ns archon.db)

(def default-db
  {:name "glow"
   :active-panel :city-input-panel   ;; the active view to show
   :city-name "city"                 ;; the city we want to limit our vendor search to
   :current-vendor-info {}           ;; information for the vendor currently being examined
   :vendor-email "email address"     ;; the email address we will send a registration invite email to
   :prev-state {}})                  ;; holds the spot we want to jump back to after registration email sent
