import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

// observable class extension
import 'rxjs/add/observable/of';

// observable operators
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import { Hero } from './hero';
import { HeroSearchService } from './hero-search.service';

@Component({
  selector: 'hero-search',
  templateUrl: './hero-search.component.html',
  styleUrls: [ './hero-search.component.css' ],
  providers: [ HeroSearchService ]
})

export class HeroSearchComponent implements OnInit  { 
  heroes: Observable<Hero[]>;
  
  private searchTerms = new Subject<string>();

  constructor(
    private heroSearchService: HeroSearchService,
    private router: Router
  ) {}
  
  ngOnInit(): void {
    this.heroes = this.searchTerms
        .debounceTime(300) // wait 300ms after each keystroke before considering the term
        .distinctUntilChanged() // ignore if next search term is same as previous
        .switchMap(term =>  
          term // switch to new observable each time the term changes
          ? this.heroSearchService.search(term) // return the http search observable
          : Observable.of<Hero[]>([]) // or the observable of empty heroes if there was no search term
        ) 
        .catch(error => {
          // TODO: add real error handling
          console.log(error);
          return Observable.of<Hero[]>([]);
        });
  }
  
  search(term: string): void {
    this.searchTerms.next(term);
  }
  
  gotoDetail(hero: Hero): void {
    let link = ['/detail', hero.id];
    this.router.navigate(link);
  }
}
