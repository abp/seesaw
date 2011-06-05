;  Copyright (c) Dave Ray, 2011. All rights reserved.

;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this 
;   distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns seesaw.examples.design-grid-figure-178
  (:use [seesaw core design-grid]))

; See http://designgridlayout.java.net/examples.html 

(defn build-form []
  (build (design-grid-panel :label-alignment :right)
    (row (grid "Name:") 			(add (text "abcdefghijklmopqrstuvwxyz")))
    (row (grid "Address:")    (add (map #(radio :text %) ["Home" "Office" "Alternate"])))
    (row (grid "Street:") 		(add (text "ABCDEFGHIJKLMNOPQRSTUVWXYZ")))
    (row (grid "City/State:") (add (text "Smallville") 2) (add (text "xx")))
    (row (grid "Zip:" 1)    	(add (text "12345")) (add 2))
    (row (grid "Phone/FAX:")  (add (text "888-555-1212") (text "888-555-1212")) (add))
    (row (grid "E-mail:" 1) 	(add (text "mailbox@domain.com") 2) (add))
    (empty-row)
		(row (grid "Interests:") 	(add (map #(checkbox :text %) ["Compilers" "Database" "Productivity"])))
		(row (grid) 							(add (map #(checkbox :text %) ["Prototyping" "Teamware" "Networking"])))
		(empty-row)
    (row center (add (button :text "Apply")) (add (button :text "Reset")))))

		
(defn app []
  (frame 
    :title "DesignGrid Example" 
    :content 
      (build-form)))

(defn -main [& args]
  (invoke-later (app)))
;(-main) 

