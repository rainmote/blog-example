(ns myns.git
  (:require [clojure.java.shell :as shell]))

(defn get-git-log-params []
  {:commit "%H"
   :abbreviated-commit "%h"
   :tree "%T"
   :abbreviated-tree "%t"
   :parent "%P"
   :abbreviated-parent "%p"
   :refs "%D"
   :encoding "%e"
   :subject "%s"
   :body "%b"
   :commit-notes "%N"
   :author-name "%aN"
   :author-email "%aE"
   :author-date "%ai"
   :author-timestamp "%at"
   :committer-name "%cN"
   :committer-email "%cE"
   :committer-date "%ci"
   :committer-timestamp "%ct"})

(def magic-item "&=&=&=&=&=&=")
(def magic-line "#@#@#@#@#@#@")

(defn git-log
  [{:keys [code-path branch]}]
  (let [[ks vs] (apply mapv vector (seq (get-git-log-params)))]
    (->> (shell/sh "bash" "-c"
                   (-> (clojure.string/join magic-item vs)
                       (str ,,, magic-line)
                       (#(format "git log remotes/origin/%s --format='%s'" branch %) ,,,))
                  :dir code-path)
        :out
        clojure.string/trim-newline
        ;; split output to line
        (#(clojure.string/split % (re-pattern magic-line)) ,,,)
        ;; split line to item
        (pmap #(clojure.string/split % (re-pattern magic-item)) ,,,)
        ;; combination ks and output into map
        ;; [:a :b] [1 2] => {:a 1 :b 2}
        (pmap #(into {} (mapv vector ks %)) ,,,)
        doall)))

(comment
  (->> (git-log {:codepath "/Users/xxx/mycode/"
                 :branch "master"})
       first)
)
