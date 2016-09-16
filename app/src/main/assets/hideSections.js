var styleTag = document.createElement("style");
styleTag.textContent =
'#wrapper_et,#qr_preview,#footer-container,#content_container,.menu,.friends_wr,.see_more_wrapper,.cookie_bar,.green_left,.add_to_contest,.above_body,.menu menu_custom mobile2,.see_more.reply_t.top_button{display: none;} #wrapper_et{visibility: hidden;} .body_wrapper.full_width {margin-top: 0px; position: relative;} .sectThBck{background-color: #ffffff;} div.thread_section_trending{background-color: #ffffff;} .forumbit_post_custom {background: #ffffff; box-shadow: 0 0 7px 2px #888888;}';
document.documentElement.appendChild(styleTag);
document.getElementsByClassName("index_sections_wp")[0].style.cssText = 'background: #ffffff !important';