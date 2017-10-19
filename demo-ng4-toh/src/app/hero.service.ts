import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';

import { LoggerService } from './logger.service';
import { Hero } from './hero';

import 'rxjs/add/operator/toPromise';

@Injectable()
export class HeroService {
  
  private heroesUrl = 'api/heroes'; // url to web api
  private headers = new Headers({'Content-Type': 'application/json'});
  
  constructor(
      private http: Http,
      private logger: LoggerService
    ) {}
  
  getHeroes(): Promise<Hero[]> {
    
    // test logging
//    this.logger.info("in getHeroes()");
//    this.logger.warn("in getHeroes()");
//    this.logger.error("in getHeroes()");
//    this.logger.invokeConsoleMethod('info', "in getHeroes()");
//    this.logger.invokeConsoleMethod('warn', "in getHeroes()");
//    this.logger.invokeConsoleMethod('error', "in getHeroes()");
    
    return this.http.get(this.heroesUrl)
              .toPromise()
              .then(response => response.json().data as Hero[])
              .catch(this.handleError);
  } 
  
  private handleError(error: any): Promise<any> {
    this.logger.error('An error occurred!!', error); // demo purpose only
    return Promise.reject(error.message || error);
  }
  
  getHeroesWithLatency(): Promise<Hero[]> {
    return new Promise(resolve => {
      setTimeout(() => resolve(this.getHeroes()), 1500);
    });
  }
  
  getHero(id: number): Promise<Hero> {
    const url = `${this.heroesUrl}/${id}`;
    //const url = '${this.heroesUrl}/${id}'; // cannot use single quote, must use backtick `
    
    this.logger.info('getHero(): url=' + url);
    
    return this.http.get(url)
        .toPromise()
        .then(response => response.json().data as Hero)
        .catch(this.handleError);
  }
  
  update(hero: Hero): Promise<Hero> {
    const url = `${this.heroesUrl}/${hero.id}`;
    
    return this.http.put(url, JSON.stringify(hero), {headers: this.headers})
        .toPromise()
        .then(() => hero)
        .catch(this.handleError);
  }
  
  create(name: string): Promise<Hero> {
    return this.http.post(this.heroesUrl, JSON.stringify({name: name}), {headers: this.headers})
        .toPromise()
        .then(res => res.json().data as Hero)
        .catch(this.handleError);
  }
  
  delete(id: number): Promise<void> {
    const url = `${this.heroesUrl}/${id}`;
    
    return this.http.delete(url, {headers: this.headers})
        .toPromise()
        .then(() => null)
        .catch(this.handleError);
  }
}