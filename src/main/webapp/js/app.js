// Small progressive enhancements; every page still works without JavaScript.
(function () {
  "use strict";

  // Confirmation for destructive forms
  document.querySelectorAll("form[data-confirm]").forEach(function (f) {
    f.addEventListener("submit", function (e) {
      if (!window.confirm(f.dataset.confirm)) e.preventDefault();
    });
  });

  document.querySelectorAll("[data-print]").forEach(function (b) {
    b.addEventListener("click", function () { window.print(); });
  });

  // Inline validation messages instead of the browser's tooltip bubbles
  function message(input) {
    var msg = input.parentNode.querySelector(".field-msg");
    if (input.dataset.match) {
      var other = input.form.elements[input.dataset.match];
      input.setCustomValidity(other && other.value !== input.value ? (input.dataset.msg || "Values do not match") : "");
    }
    var bad = !input.checkValidity();
    input.classList.toggle("bad", bad);
    if (bad) {
      if (!msg) {
        msg = document.createElement("span");
        msg.className = "field-msg";
        input.insertAdjacentElement("afterend", msg);
      }
      msg.textContent = input.dataset.msg || input.validationMessage;
    } else if (msg) {
      msg.remove();
    }
    return !bad;
  }

  document.querySelectorAll("form[data-validate], form[data-marksheet]").forEach(function (form) {
    form.noValidate = true;
    var fields = form.querySelectorAll("input:not([type=hidden]), select");
    fields.forEach(function (i) {
      i.addEventListener("blur", function () { message(i); });
      i.addEventListener("input", function () { if (i.classList.contains("bad")) message(i); });
    });
    form.addEventListener("submit", function (e) {
      var ok = true, first = null;
      fields.forEach(function (i) {
        if (!message(i)) { ok = false; first = first || i; }
      });
      if (!ok) { e.preventDefault(); first.focus(); }
    });
  });

  // Live percentage and grade on the mark sheet
  function grade(p) {
    return p >= 90 ? "A+" : p >= 80 ? "A" : p >= 70 ? "B" : p >= 60 ? "C" : p >= 40 ? "D" : "F";
  }
  var sheet = document.querySelector("form[data-marksheet]");
  if (sheet) {
    var dirty = sheet.querySelector("[data-dirty]");
    var changed = false;
    sheet.querySelectorAll("input.mark").forEach(function (input) {
      var row = input.closest("tr");
      var start = input.value;
      input.addEventListener("input", function () {
        var v = parseFloat(input.value), max = parseFloat(input.dataset.max);
        var pctCell = row.querySelector("[data-pct]"), gradeCell = row.querySelector("[data-grade]");
        if (input.value === "" || isNaN(v) || v < 0 || v > max) {
          pctCell.textContent = ""; gradeCell.textContent = "";
        } else {
          var p = v * 100 / max;
          pctCell.textContent = p.toFixed(1);
          gradeCell.textContent = grade(p);
          gradeCell.classList.toggle("neg", p < 40);
        }
        changed = true;
        if (dirty) dirty.hidden = input.value === start && !changed;
      });
    });
    window.addEventListener("beforeunload", function (e) {
      if (changed) { e.preventDefault(); e.returnValue = ""; }
    });
    sheet.addEventListener("submit", function () { changed = false; });
  }

  // Filter the student picker
  document.querySelectorAll("[data-filter]").forEach(function (input) {
    var list = document.querySelector(input.dataset.filter);
    input.addEventListener("input", function () {
      var q = input.value.trim().toLowerCase();
      list.querySelectorAll("li").forEach(function (li) {
        li.hidden = q !== "" && li.textContent.toLowerCase().indexOf(q) < 0;
      });
    });
  });
})();
