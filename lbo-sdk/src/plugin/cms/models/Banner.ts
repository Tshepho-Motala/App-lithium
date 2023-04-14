export class Banner {
    id!: number;
    version: number = 0;
    name: string;
    imageUrl: string;
    startDate: string | null = null;
    timeFrom: string | null = null;
    timeTo: string | null = null;
    link: string = '';
    recurrencePattern: string = '';
    loggedIn: boolean | null;
    termsUrl!: string;
    displayText!: string;
    lengthInDays: number = 1;
    singleDay!: boolean;
    deleted: boolean = false;
    timezone: string | undefined

    constructor(name: string, imageUrl: string) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.loggedIn = null;
    }
}
