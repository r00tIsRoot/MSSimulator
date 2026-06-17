package com.mssimulator.data

/**
 * Built-in sample skill data for offline/demo use.
 * In production, this would be fetched from GitHub JSON.
 */
val SAMPLE_SKILLS_JSON = """{"version":"1.0.0","jobs":{
"히어로":[{"id":"h1","name":"레이징 블로우","job":"히어로","damage":550,"hit_count":6,"cooltime":0,"delay":720,"is_attack":true},{"id":"h2","name":"인사이징","job":"히어로","damage":450,"hit_count":4,"cooltime":0,"delay":630,"is_attack":true},{"id":"h3","name":"소드 일루전","job":"히어로","damage":350,"hit_count":3,"cooltime":10000,"delay":540,"is_summon":true},{"id":"h4","name":"레이지 업라이징","job":"히어로","damage":750,"hit_count":8,"cooltime":45000,"delay":900,"is_attack":true},{"id":"h5","name":"인스톨 실드","job":"히어로","damage":200,"hit_count":1,"cooltime":60000,"delay":600,"duration":180000,"is_buff":true},{"id":"h6","name":"컴뱃 레디","job":"히어로","damage":0,"hit_count":0,"cooltime":90000,"delay":0,"duration":30000,"is_buff":true}],
"아크메이지(불/독)":[{"id":"fp1","name":"도트 퍼니셔","job":"아크메이지(불/독)","damage":550,"hit_count":8,"cooltime":8000,"delay":720,"is_attack":true},{"id":"fp2","name":"미스트 이럽션","job":"아크메이지(불/독)","damage":500,"hit_count":6,"cooltime":10000,"delay":780,"is_attack":true},{"id":"fp3","name":"파이어 오라","job":"아크메이지(불/독)","damage":300,"hit_count":3,"cooltime":0,"delay":0,"is_buff":true},{"id":"fp4","name":"메테오","job":"아크메이지(불/독)","damage":800,"hit_count":10,"cooltime":45000,"delay":900,"is_attack":true},{"id":"fp5","name":"헤이즈","job":"아크메이지(불/독)","damage":350,"hit_count":4,"cooltime":12000,"delay":600,"is_attack":true},{"id":"fp6","name":"인피니티","job":"아크메이지(불/독)","damage":0,"hit_count":0,"cooltime":180000,"delay":0,"duration":40000,"is_buff":true}],
"나이트로드":[{"id":"nl1","name":"쇼다운 챌린지","job":"나이트로드","damage":350,"hit_count":4,"cooltime":0,"delay":600,"is_attack":true},{"id":"nl2","name":"마크 오브 나이트로드","job":"나이트로드","damage":400,"hit_count":5,"cooltime":0,"delay":540,"is_attack":true},{"id":"nl3","name":"쿼드러플 스로우","job":"나이트로드","damage":500,"hit_count":4,"cooltime":0,"delay":660,"is_attack":true},{"id":"nl4","name":"다크 플레어","job":"나이트로드","damage":250,"hit_count":2,"cooltime":12000,"delay":480,"duration":40000,"is_summon":true},{"id":"nl5","name":"스프레드 스로우","job":"나이트로드","damage":700,"hit_count":8,"cooltime":45000,"delay":900,"is_attack":true},{"id":"nl6","name":"쉐도우 파트너","job":"나이트로드","damage":0,"hit_count":0,"cooltime":60000,"delay":0,"duration":180000,"is_buff":true}]
}}"""

val SAMPLE_BOSSES_JSON = """{"version":"1.0.0","bosses":[
{"id":"nlucid","name":"노말 루시드","hp":3000000000000,"level":230,"defense":300},
{"id":"hlucid","name":"하드 루시드","hp":12000000000000,"level":250,"defense":380},
{"id":"nwill","name":"노말 윌","hp":3500000000000,"level":235,"defense":300},
{"id":"hwill","name":"하드 윌","hp":14000000000000,"level":255,"defense":380},
{"id":"ndnell","name":"노말 듄켈","hp":7000000000000,"level":245,"defense":300},
{"id":"cgloom","name":"카오스 글로움","hp":8000000000000,"level":250,"defense":380},
{"id":"seren","name":"세렌","hp":30000000000000,"level":265,"defense":380},
{"id":"kalos","name":"카로스","hp":80000000000000,"level":270,"defense":380}
]}"""
