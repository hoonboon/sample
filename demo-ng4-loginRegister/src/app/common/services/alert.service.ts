import { Injectable } from '@angular/core';
import { Router, NavigationStart } from '@angular/router';
import { Observable } from 'rxjs';
import { Subject } from 'rxjs/Subject';


@Injectable()
export class AlertService {
  private subject = new Subject<any>();
  
  private keepAfterNavigateChange = false;
  
  constructor(private router: Router) {
    // clear alert on route change
    router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        if (this.keepAfterNavigateChange) {
          // only keep for a single location change
          this.keepAfterNavigateChange = false;
        } else {
          // clear alert
          this.subject.next();
        }
      }
    });
  }

  getMessage(): Observable<any> {
    return this.subject.asObservable();
  }
  
  success(message: string, keepAfterNavigateChange = false) {
    this.keepAfterNavigateChange = keepAfterNavigateChange;
    this.subject.next({ type: 'success', text: message });
  }
  
  error(message: string, keepAfterNavigateChange = false) {
    this.keepAfterNavigateChange = keepAfterNavigateChange;
    this.subject.next({ type: 'error', text: message });
  }
  
}