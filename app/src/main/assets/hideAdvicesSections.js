var x = document.getElementsByClassName("header");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("menu");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("breadcrumb");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("col-md-3 mb30");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("row");
x[2].parentNode.removeChild(x[2]);

x = document.getElementsByClassName("footer");
x[0].parentNode.removeChild(x[0]);

x = document.getElementsByClassName("col-md-9");
x[0].style.width = "100%";

x = document.getElementsByClassName("col-md-4");
for (i = 0; i < x.length; i++) {
    x[i].style.width = "50%";
}

x = document.getElementsByTagName("h3");
for (i = 0; i < x.length; i++) {
    x[i].style.fontSize = "30px";
    x[i].style.height = "80px"
}

document.getElementsByTagName("h2")[0].style.fontSize = "30px"
document.getElementsByTagName("h2")[0].style.marginLeft = "20px"

document.body.style.backgroundColor = "white";




