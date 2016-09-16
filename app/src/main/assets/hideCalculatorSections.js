var x = document.getElementsByClassName("header");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("menu");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("footer");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("col-md-3");
var length = x.length;
for (i = 0; i < length; i++) {
    x[0].parentNode.removeChild(x[0]);
}


