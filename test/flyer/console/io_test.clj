(ns flyer.console.io-test
  (:require [flyer.console.io :as console]
            [clojure.test :refer [deftest testing is]]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [clojure.string :as str]))


(deftest ask
  (let [input "12"
        question "6 + 6 is = "]
    (testing "should return de provided input"
      (is (match? input
                  (with-in-str input (console/ask "some question:")))))
    (testing "Should print the provided question"
      (is (match? question
                  (with-out-str (with-in-str input (console/ask question))))))))

(deftest produce-result-route-string
  (let [routing-path [:gru :brc :scl :orl :cdg]
        routing-value 30.0
        routing-result {:path routing-path :cost routing-value}
        result (console/to-result-route-str routing-result)]
    (testing "Should produce a normalized result string"
      (let [[path-str value-str] (str/split result #" > ")
            value (-> value-str
                      (str/replace #"\$" "")
                      read-string
                      float)
            path (->> (str/split path-str #" - ")
                      (map str/lower-case)
                      (map keyword)
                      (into []))]

        (is (match? (m/equals {:path path :cost value})
                    routing-result))))))

(deftest print-result-route
  (let [routing-path [:gru :brc :scl :orl :cdg]
        routing-value 30.0
        routing-result {:path routing-path :cost routing-value}
        result (with-out-str (console/print-result routing-result))]
    (testing "Should print a normalized result string"
      (let [[path-str value-str] (str/split result #" > ")
            value (-> value-str
                      (str/replace #"\$" "")
                      read-string
                      float)
            path (->> routing-path
                      (map name)
                      (map str/upper-case)
                      (str/join " - ")
                      (str "Best route: "))]

        (is (match? value
                    routing-value))

        (is (match? path-str
                    path))))))

(deftest print-route-not-found
  (let [origin :abc
        destination :def
        output (with-out-str (console/print-not-found origin destination))]
    (testing "Should print formatted result"
      (is (match? "No routes found for ABC-DEF\n" output)))))
