const noticeTextVersion='v1';
const cookieName='portal_notice';
function accepted(){return document.cookie.split('; ').some(x=>x.startsWith(`${cookieName}=${noticeTextVersion}:`))}
function update(){const ok=accepted();document.querySelector('#notice').hidden=ok;document.querySelector('#content').hidden=!ok}
document.querySelector('#accept').addEventListener('click',()=>{document.cookie=`${cookieName}=${noticeTextVersion}:${Date.now()}; Max-Age=2592000; Path=/; SameSite=Strict`;update()});
update();
